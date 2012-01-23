(ns home-sweet-home.routing
  (:use [home-sweet-home web core]))

(defn edit-article-request [req]
  {:id (Integer/parseInt (get (:params req) "id"))
   :title (get (:params req) "new-title")
   :content (get (:params req) "new-content")})


(def get-request-handlers
  {"/" {:controller (fn [req] {"Impressum" "/impressum"
                              "Blog" "/blog"
                              "Create" "/create"})
        :presenter present-index-page}
   "/impressum" {:interactor get-contact-information
                 :presenter present-contact-information}
   "/blog" {:interactor list-all-articles
            :presenter present-all-articles}
   "/article" {:interactor get-article
               :controller #(Integer/parseInt (get (:params %) "id"))
               :presenter present-blog}
   "/create" {:presenter present-create-article}
   "/edit" {:interactor get-article
            :controller #(Integer/parseInt (or (get (:params %) "id") "-1"))
            :presenter present-edit-article}})

(def post-request-handlers
  {"/create" {:interactor save-article
              :controller (fn [req]
                          {:title (get (:params req) "title")
                           :content (get (:params req) "content")})
              :presenter present-save}
   "/edit" {:interactor edit-article
            :controller edit-article-request
            :presenter redirect-to-article}})

(defn choose-handler [{:keys [uri request-method]}]
  (if (not (or (get-request-handlers uri)
               (post-request-handlers uri)))
    {:presenter present-request-information}
    (if (= :post request-method)
      (post-request-handlers uri)
      (get-request-handlers uri))))

(defn handler-call [handler req db]
  (if-not (or (:interactor handler) (:controller handler))
    ((:presenter handler) req)
    (let [interactor (if-let [interactor (:interactor handler)]
                       interactor
                       (fn [req db] req))
          controller (:controller handler)
          presenter (:presenter handler)]
      (if controller
        (presenter (interactor (controller req) db))
        (presenter (interactor db))))))

(defn handle [req db]
  (let [handler (choose-handler req)]
    (handler-call handler req db)))
