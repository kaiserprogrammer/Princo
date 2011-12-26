(ns home-sweet-home.main
  (:use home-sweet-home.core)
  (:use [home-sweet-home.web])
  (:use ring.adapter.jetty)
  (:use [ring.middleware reload stacktrace params]))

(def get-request-handlers
  {"/" {:interactor (fn [req presenter] (presenter req))
        :controller (fn [req] {"Impressum" "/impressum"
                              "Blog" "/blog"})}
   "/impressum" {:interactor get-contact-information}
   "/blog" {:interactor list-all-articles}
   "/article" {:interactor get-article
               :controller #(Integer/parseInt (get (:params %) "id"))}
   "/save" {:interactor save-article
            :controller (fn [req]
                          {:title (get (:params req) "title")
                           :content (get (:params req) "content")})}
   "/edit" {:interactor get-article
            :controller #(Integer/parseInt (get (:params %) "id"))}})

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
       (let [handle (get-request-handlers (:uri req))
             interactor (:interactor handle)
             controller (:controller handle)
             presenter (:controller handle)]
         (if (= (:uri req) "/")
           (interactor (controller req) present-index-page)
           (if (= (:uri req) "/impressum")
             (interactor present-contact-information)
             (if (= (:uri req) "/blog")
               (interactor present-all-articles)
               (if (= (:uri req) "/article")
                 (interactor
                  (controller req)
                  present-blog)
                 (if (= (:uri req) "/save")
                   (interactor
                    (controller req)
                     present-save)
                   (if (= (:uri req) "/edit")
                     (interactor
                      (controller req)
                      present-edit-article)
                     nil))))))))}))

(def app
  (-> #'handler
      (wrap-params)
      (wrap-reload '[home-sweet-home.main])
      (wrap-stacktrace)))
