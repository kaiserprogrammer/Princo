(ns home-sweet-home.gateway
  (:import [java.io File]))

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

(defrecord FileSystemDB [path]
  EntityGateway

  (retrieve-article [this id]
    (let [f (File. path (str id))]
      (when (.exists f)
        (read-string (slurp f)))))

  (create-article [this title content]
    (do (spit (File. path
                     (str
                      (inc
                       (reduce max
                               (conj (map read-string (seq (.list
                                                            (File. path)))) -1)))))
              (str {:title title :content content}))
        'done))

  (count-articles [this] (count (seq (.list (File. path)))))

  (retrieve-all-articles [this]
    (reduce (fn [res id]
              (conj res (retrieve-article this id)))
            []
            (seq (.list (File. path)))))

  (update-article [this id title content]
    (spit (File. path (str id))
          (str {:title title :content content}))))