(ns home-sweet-home.main
  (:use home-sweet-home.core)
  (:use [home-sweet-home.web])
  (:use ring.adapter.jetty)
  (:use [ring.middleware reload stacktrace params]))

(def request-handlers
  {"/" present-index-page
   "/impressum" get-contact-information
   "/blog" list-all-articles
   "/save" save-article
   "/edit" get-article})

(defn handler [req]
  (if (= (:uri req) "/")
    {:status 200
     :headers {}
     :body ((request-handlers "/") {"Impressum" "/impressum"
                                    "Blog" "/blog"})}
    (if (= (:uri req) "/impressum")
     {:status 200
      :headers {}
      :body ((request-handlers "/impressum") present-contact-information)}
     (if (= (:uri req) "/blog")
       {:status 200
        :headers {}
        :body (if-let [article-id (get (:params req) "article")]
                (get-article
                 (Integer/parseInt article-id)
                 present-blog)
                ((request-handlers "/blog") present-all-articles))}
       (if (= (:uri req) "/save")
         {:status 200
          :headers {}
          :body ((request-handlers "/save")
                 (get (:params req) "title")
                 (get (:params req) "content")
                 present-save)}
         (if (and (= :get (:request-method req)) (= (:uri req) "/edit"))
           {:status 200
            :headers {}
            :body ((request-handlers "/edit")
                   0
                   present-edit-article)}
           (if (and (= :post (:request-method req)) (= (:uri req) "/edit"))
             {:status 200
              :headers {}
              :body (edit-article 0
                                  (get (:params req) "new-title")
                                  (get (:params req) "new-content")
                                  present-blog)}
             {:status 200
             :headers {"Content-type" "text/plain"}
             :body (str req)})))))))

(def app
  (-> #'handler
      (wrap-params)
      (wrap-reload '[home-sweet-home.main])
      (wrap-stacktrace)))
