(ns home-sweet-home.main
  (:use home-sweet-home.core)
  (:use [home-sweet-home.web])
  (:use ring.adapter.jetty)
  (:use [ring.middleware reload stacktrace params]))

(def get-request-handlers
  {"/" present-index-page
   "/impressum" get-contact-information
   "/blog" list-all-articles
   "/save" save-article
   "/edit" get-article})

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
       (if (= (:uri req) "/")
         ((get-request-handlers "/") {"Impressum" "/impressum"
                                      "Blog" "/blog"})
         (if (= (:uri req) "/impressum")
           ((get-request-handlers "/impressum") present-contact-information)
           (if (= (:uri req) "/blog")
             (if-let [article-id (get (:params req) "article")]
               (get-article
                (Integer/parseInt article-id)
                present-blog)
               ((get-request-handlers "/blog") present-all-articles))
             (if (= (:uri req) "/save")
               ((get-request-handlers "/save")
                (get (:params req) "title")
                (get (:params req) "content")
                present-save)
               (if (and (= :get (:request-method req)) (= (:uri req) "/edit"))
                 ((get-request-handlers "/edit")
                  0
                  present-edit-article)
                 (when (and (= :post (:request-method req)) (= (:uri req) "/edit")))))))))}))

(def app
  (-> #'handler
      (wrap-params)
      (wrap-reload '[home-sweet-home.main])
      (wrap-stacktrace)))
