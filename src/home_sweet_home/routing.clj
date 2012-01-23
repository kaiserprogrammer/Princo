(ns home-sweet-home.routing
  (:use [home-sweet-home web core]))

(defn edit-article-request [req]
  {:id (Integer/parseInt (get (:params req) "id"))
   :title (get (:params req) "new-title")
   :content (get (:params req) "new-content")})


(def request-handlers
  {["/" :get]
   {:controller (fn [req] {"Impressum" "/impressum"
                          "Blog" "/blog"
                          "Create" "/create"})
    :presenter present-index-page}
   ["/impressum" :get]
   {:interactor get-contact-information
    :presenter present-contact-information}
   ["/blog" :get]
   {:interactor list-all-articles
    :presenter present-all-articles}
   ["/article" :get]
   {:interactor get-article
    :controller #(Integer/parseInt (get (:params %) "id"))
    :presenter present-blog}
   ["/create" :get]
   {:presenter present-create-article}
   ["/edit" :get]
   {:interactor get-article
    :controller #(Integer/parseInt (or (get (:params %) "id") "-1"))
    :presenter present-edit-article}
   ["/create" :post]
   {:interactor save-article
    :controller (fn [req]
                  {:title (get (:params req) "title")
                   :content (get (:params req) "content")})
    :presenter present-save}
   ["/edit" :post]
   {:interactor edit-article
    :controller edit-article-request
    :presenter redirect-to-article}})

(defn choose-handler [{:keys [uri request-method]} request-handlers]
  (let [search [uri request-method]]
    (if-let [handler (request-handlers search)]
      handler
      {:presenter present-request-information})))

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
  (let [handler (choose-handler req request-handlers)]
    (handler-call handler req db)))
