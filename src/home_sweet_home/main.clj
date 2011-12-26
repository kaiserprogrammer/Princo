(ns home-sweet-home.main
  (:use home-sweet-home.core)
  (:use [home-sweet-home.web])
  (:use ring.adapter.jetty)
  (:use [ring.middleware reload stacktrace params]))

(def get-request-handlers
  {"/" {:interactor present-index-page}
   "/impressum" {:interactor get-contact-information}
   "/blog" {:interactor list-all-articles}
   "/save" {:interactor save-article}
   "/edit" {:interactor get-article}
   "/article" {:interactor get-article}})

(defn edit-article-request [req]
  {:id 0
   :title (get (:params req) "new-title")
   :content (get (:params req) "new-content")})

(def post-request-handlers
  {"/edit" {:interactor edit-article
            :controller edit-article-request
            :presenter present-blog}})


(defn handler [req]
  (if (not (get-request-handlers (:uri req)))
    {:status 200
     :headers {"Content-type" "text/plain"}
     :body (str req)}
    {:status 200
     :headers {}
     :body
     (if (= :post (:request-method req))
       ((:interactor (post-request-handlers "/edit"))
        ((:controller (post-request-handlers "/edit")) req)
        (:presenter (post-request-handlers "/edit")))
       (let [handle (get-request-handlers (:uri req))]
         (if (= (:uri req) "/")
           ((:interactor handle) {"Impressum" "/impressum"
                      "Blog" "/blog"})
           (if (= (:uri req) "/impressum")
             ((:interactor handle) present-contact-information)
             (if (= (:uri req) "/blog")
               ((:interactor handle) present-all-articles)
               (if (= (:uri req) "/article")
                 ((:interactor handle)
                     (Integer/parseInt (get (:params req) "id"))
                   present-blog)
                 (if (= (:uri req) "/save")
                   ((:interactor handle)
                       {:title (get (:params req) "title")
                        :content (get (:params req) "content")}
                     present-save)
                   (if (= (:uri req) "/edit")
                     ((:interactor handle)
                         0
                       present-edit-article)
                     (when (and (= :post (:request-method req)) (= (:uri req) "/edit")))))))))))}))

(def app
  (-> #'handler
      (wrap-params)
      (wrap-reload '[home-sweet-home.main])
      (wrap-stacktrace)))
