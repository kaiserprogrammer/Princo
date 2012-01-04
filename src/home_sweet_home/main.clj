(ns home-sweet-home.main
  (:use home-sweet-home.core)
  (:use [home-sweet-home.web])
  (:use ring.adapter.jetty)
  (:use [ring.middleware reload stacktrace params])
  (:import home_sweet_home.gateway.InMemoryDB))

(def db (InMemoryDB. (atom [])))

(def get-request-handlers
  {"/" {:controller (fn [req] {"Impressum" "/impressum"
                              "Blog" "/blog"
                              "Create" "/edit"})
        :presenter present-index-page}
   "/impressum" {:interactor get-contact-information
                 :presenter present-contact-information}
   "/blog" {:interactor list-all-articles
            :presenter present-all-articles}
   "/article" {:interactor get-article
               :controller #(Integer/parseInt (get (:params %) "id"))
               :presenter present-blog}
   "/save" {:interactor save-article
            :controller (fn [req]
                          {:title (get (:params req) "title")
                           :content (get (:params req) "content")})
            :presenter present-save}
   "/edit" {:interactor get-article
            :controller #(Integer/parseInt (or (get (:params %) "id") "-1"))
            :presenter present-edit-article}})

(defn edit-article-request [req]
  {:id (Integer/parseInt (get (:params req) "id"))
   :title (get (:params req) "new-title")
   :content (get (:params req) "new-content")})

(def post-request-handlers
  {"/edit" {:interactor edit-article
            :controller edit-article-request
            :presenter present-blog}})


(defn handler-call [handle req]
  (let [interactor (if-let [interactor (:interactor handle)]
                     interactor
                     (fn [req db] req))
        controller (:controller handle)
        presenter (:presenter handle)]
    (if controller
      (presenter (interactor (controller req) db))
      (presenter (interactor db)))))

(defn choose-handler [{:keys [uri method]} ]
  (if (not (or (get-request-handlers uri)
               (post-request-handlers uri)))
    {:presenter present-request-information}
    (if (= :post method)
      (post-request-handlers uri)
      (get-request-handlers uri))))

(defn handle [req]
  (let [handler (choose-handler req)]
    (handler-call handle req)))

(def app
  (-> #'handle
      (wrap-params)
      (wrap-reload '[home-sweet-home.main])
      (wrap-stacktrace)))
