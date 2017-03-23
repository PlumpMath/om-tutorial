(ns om-tutorial.core
  (:require
    [goog.dom :as gdom]
    [om.next :as om :refer-macros [defui]]
    [om.dom :as dom]
    [cljs.pprint :as pprint]
    [thosmos.core :as ts]
    [datascript.core :as d]
    ;[shodan.console :as l :include-macros true]
    )
  (:require-macros
    [cljs-log.core :as l :refer [debug info warn severe]])
  )

(enable-console-print!)

; example data from
; https://github.com/omcljs/om/wiki/Components%2C-Identity-%26-Normalization
;(def init-data
;  {:list/one [{:name "John" :points 0}
;              {:name "Mary" :points 0}
;              {:name "Bob"  :points 0}]
;   :list/two [{:name "Mary" :points 0 :age 27}
;              {:name "Gwen" :points 0}
;              {:name "Jeff" :points 0}]})



(def conn
  (d/create-conn
    {:app/id       {:db/unique :db.unique/identity}
     :list/items     {:db/valueType :db.type/ref :db/cardinality :db.cardinality/many}
     :dashboard/list {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
     :type           {:db/index true}
     :content/type   {:db/index true}}))


(d/transact! conn
  [{:db/id        -1
    :type         :type/content
    :content/type :blog/post
    :author       "Laura Smith"
    :title        "A Post!"
    :content      "Lorem ipsum dolor sit amet, quem atomorum te quo"
    :favorites    0}
   {:db/id        -2
    :type         :type/content
    :content/type :asset/photo
    :title        "A Photo!"
    :image        "photo.jpg"
    :caption      "Lorem ipsum"
    :favorites    0}
   {:db/id        -3
    :type         :type/content
    :content/type :blog/post
    :author       "Jim Jacobs"
    :title        "Another Post!"
    :content      "Lorem ipsum dolor sit amet, quem atomorum te quo"
    :favorites    0}
   {:db/id        -4
    :type         :type/content
    :content/type :asset/graphic
    :title        "Charts and Stufff!"
    :image        "chart.jpg"
    :favorites    0}
   {:db/id        -5
    :type         :type/content
    :content/type :blog/post
    :author       "May Fields"
    :title        "Yet Another Post!"
    :content      "Lorem ipsum dolor sit amet, quem atomorum te quo"
    :favorites    0}
   {:db/id      -6
    :db/ident   :content/list
    :type       :type/list
    :list/title "A List of Various Media"
    :list/items [-1 -2 -3 -4 -5]
    :list/type  :type/content}
   {:db/id           -7
    :db/ident        :dashboard/root
    :type            :type/app
    :dashboard/title "The Root"
    :dashboard/list  -6}])

;(d/q '[:find [?e] :where [?e :content/type :blog/post]] (d/db conn))

(debug "LOADING ...")

(defmulti read om/dispatch)

(defmethod read :default
  [{:keys [state query] :as env} k _]
  (let [db (d/db state)]                                    ;; CACHING!!!
    (l/debug "READ DEFAULT key" k)
    (l/debug "READ DEFAULT query" query)
    ;(debug (keys env))
    ;(d/q '[:find [(pull ?e [*]) ...] :where [?e :type :type/content]] (d/db conn))
    {:value nil}
    ))

;;; assumed to be called with a query or :type lookup key, otherwise it would return everything in the DB
(defmethod read :db/id
  [{:keys [state query] :as env} key params]
  (l/debug "READ DB/ID query" query)
  (l/debug "READ DB/ID params" params)
  {:value nil}
)

;;; assumed to be called with a query or :type lookup key, otherwise it would return everything in the DB
(defmethod read :db/ident
  [{:keys [state query] :as env} key params]
  (l/debug "DB/IDENT ast" (:ast env))
  (l/debug "DB/IDENT query" query)
  (l/debug "DB/IDENT params" params)
  (let [value (d/pull (d/db state) query [:db/ident (:ident-ref params)])]
    (debug "DB/IDENT result" value)
    {:value value})
  )
;(if (contains? st k)
;  {:value (get st k)}
;  {:remote true})

;(defmethod read :posts
;  [{:keys [state query] :as env} k _]
;  (let [db (d/db state)]                                    ;; CACHING!!!
;    (debug "READ :posts query" query)
;    (debug "env keys " (keys env))
;
;    (let [posts (d/q '[:find [(pull ?e ?query) ...] :in $ ?query :where [?e :content/type :blog/post]] (d/db conn) query)]
;      (debug "query results:" posts)
;      {:value posts})))

(def reconciler
  (om/reconciler
    {:state  conn
     ;:normalize true
     :parser (om/parser {:read read})}))
;:send      (util/transit-post "/api")


(defui Post
  static om/Ident
  (ident [this {:keys [:db/id] :as props}]
    (let [_ (debug "Post Ident" id "params:" props)]
      id))
  static ts/ITypeKey
  (type-key [this] {:content/type :blog/post})
  static om/IQuery
  (query [this]
    ;'[*]
    [:db/id :type :content/type :title :author :content])
  Object
  (render [this]
    (let [query (om/get-query Post)
          {:keys [title] :as props} (om/props this)]
      (l/debug "Post props" props)
      (l/debug "Post query" query)
      (dom/div nil title
        (dom/ul nil
          (for [prop props]
            (dom/li nil
              (str (key prop) ": " (val prop)))))

        ))))

 (defui Photo
   static om/Ident
   (ident [this {:keys [:db/id]}] id)
   static ts/ITypeKey
   (type-key [this] {:content/type :asset/photo})
   static om/IQuery
   (query [this]
     [:db/id :type :title :image :caption])
   Object
   (render [this]
     (dom/div nil "Photo")))

 (defui Graphic
   static om/Ident
   (ident [this {:keys [:db/id]}] id)
   static ts/ITypeKey
   (type-key [this] {:content/type :asset/graphic})
   static om/IQuery
   (query [this]
     [:db/id :type :title :image])
   Object
   (render [this]
     (dom/div nil "Graphic")))

 (defui ItemList
   ;static om/Ident
   ;(ident [this props] [:db/ident :content/list])
   ;static ts/ITypeKey
   ;(type-key [this] {:type :type/list})
   ;static ts/ICollTypeKey
   ;(coll-key [this] :content/type)
   ;static om/IQueryParams
   ;(params [this] {:id-ref nil})
   static om/IQuery
   (query [this]
     ;[:db/id :list/title {:list/items (ts/get-union (with-meta [Post Photo Graphic] {:coll-key (ts/get-coll-key this)}) [:favorites])}]
     `[
       ;({:db/id [:db/id :list/title]} {:id-ref ?id-ref})
      {:list/items
       ;(ts/get-union )
       {:post    ~(om/get-query Post)
        :photo   ~(om/get-query Photo)
        :graphic ~(om/get-query Graphic)}}])
   Object
   (render [this]
     (dom/div nil
       (get (om/props this) :list/title)
       ;(ts/render this :list/items)
       )))

 (defui Dashboard
   static om/Ident
   (ident [this props] [:db/ident :dashboard/root])
   static om/IQuery
   (query [this]
     `[({:db/ident [:dashboard/title]} {:ident-ref :dashboard/root}) {:list/items ~(om/get-query ItemList)}])
 ;{:dashboard/list (ts/get-query ItemList)}
   Object
   (render [this]
     (dom/div nil
       (dom/div nil "TypeKey experiment")
       (dom/br nil nil)
       (dom/div nil (get-in (om/props this) [:db/ident :dashboard/title])))))
       ;(ts/render this ItemList)

 (def dashboard (om/factory Dashboard))

(defui App
  static om/IQuery
  (query [this]
    [{:posts (om/get-query Post)}])
  Object
  (render [this]
    (let [posts (:posts (om/props this))]
      (dom/div nil
        (dom/div nil "TypeKey experiment")
        (dom/br nil nil)
        ((om/factory ItemList))
        (debug "ItemList QUERY" (om/get-query ItemList))
        ;(for [post posts] ((om/factory Post) post))
        ))))
;(dashboard)
; (js/console.log "component?"))))
; (om/component?
;   (om/factory Post))))))
;(dashboard {:dashboard/title "yes"}))))))

(def app (om/factory App))

(om/add-root! reconciler Dashboard (gdom/getElement "app"))


;(defui Campaign
;  static om/Ident
;  (ident [this {:keys [id]}] [:db/id id])
;  static ts/ITypeKey
;  (type-key [this] :dt/type)
;  static om/IQuery
;  (query [this]
;    [:db/id :dt/type :c/name :c/desc :c/goal])
;  Object
;  (render [this]
;    (dom/div nil "Campaign")))
