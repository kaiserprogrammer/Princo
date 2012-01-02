(ns home-sweet-home.main
  (:use home-sweet-home.core)
  (:use [home-sweet-home.web])
  (:use ring.adapter.jetty)
  (:use [ring.middleware reload stacktrace params]))

(def get-request-handlers
  {"/" {:controller (fn [req] {"Impressum" "/impressum"
                              "Blog" "/blog"})
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
                     (fn [req presenter] (presenter req)))
        controller (:controller handle)
        presenter (:presenter handle)]
    (if controller
      (interactor (controller req) presenter)
      (interactor presenter))))

(defn handler [req]
  (if (not (or (get-request-handlers (:uri req))
               (post-request-handlers (:uri req))))
    {:status 200
     :headers {"Content-type" "text/plain"}
     :body (str req)}
    {:status 200
     :headers {}
     :body
     (if (= :post (:request-method req))
       (handler-call (post-request-handlers (:uri req)) req)
       (handler-call (get-request-handlers (:uri req)) req))}))

(def app
  (-> #'handler
      (wrap-params)
      (wrap-reload '[home-sweet-home.main])
      (wrap-stacktrace)))
