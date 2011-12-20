(ns home-sweet-home.main
  (:use home-sweet-home.core)
  (:use [home-sweet-home.web :only [present-contact-information present-blog]])
  (:use ring.adapter.jetty)
  (:use [ring.middleware reload stacktrace params]))

(defn handler [req]
  (if (= (:uri req) "/impressum")
    {:status 200
     :headers {}
     :body (get-contact-information present-contact-information)}
    (if (= (:uri req) "/blog")
      {:status 200
       :headers {}
       :body (get-article
              (Integer/parseInt
               (get (:params req) "article"))
              present-blog)}
      (if (= (:uri req) "/save")
        {:status 200
         :headers {}
         :body (save-article
                (get (:params req) "title")
                (get (:params req) "content")
                present-save)}
       {:status 200
        :headers {"Content-type" "text/plain"}
        :body (str req)}))))

(def app
  (-> #'handler
      (wrap-params)
      (wrap-reload '[home-sweet-home.main])
      (wrap-stacktrace)))
