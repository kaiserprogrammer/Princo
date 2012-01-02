(ns home-sweet-home.gateway)

(defprotocol EntityGateway
  (retrieve-article [this id])
  (create-article [this title content])
  (count-articles [this])
  (retrieve-all-articles [this])
  (update-article [this id title content]))

(defrecord InMemoryDB [blog]
    EntityGateway
    (retrieve-article [this id]
      (when (and (< id (.length @blog)) (>= id 0))
        (@blog id)))
    (create-article [this title content]
      (swap! blog conj {:title title
                        :content content}))
    (count-articles [this]
      (.length @blog))
    (retrieve-all-articles [this] @blog)
    (update-article [this id title content]
      (swap! blog assoc id {:title title
                            :content content})))

