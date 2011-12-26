(ns home-sweet-home.core)

(defprotocol EntityGateway
  (retrieve-article [this id])
  (create-article [this title content])
  (count-articles [this])
  (retrieve-all-articles [this])
  (update-article [this id title content]))

(defrecord InMemoryDB [blog]
    EntityGateway
    (retrieve-article [this id]
      (@blog id))
    (create-article [this title content]
      (swap! blog conj {:title title
                        :content content}))
    (count-articles [this]
      (.length @blog))
    (retrieve-all-articles [this] @blog)
    (update-article [this id title content]
      (swap! blog assoc id {:title title
                            :content content})))

(def db (InMemoryDB. (atom [])))

(defn save-article [article presenter]
  (let [title (:title article)
        content (:content article)]
   (do (create-article db title content)
       (presenter {:success true}))))

(defn get-article [article-id presenter]
  (presenter
   (when (and (>= article-id 0) (< article-id (count-articles db)))
     (retrieve-article db article-id))))

(defn get-contact-information [presenter]
  (presenter
   {:name "Jürgen Bickert"
    :street "Grafenspitz 11"
    :city "94099 Ruhstorf"
    :phone "08531/249164"
    :email "juergenbickert@gmail.com"}))

(defn list-all-articles [presenter]
  (presenter (retrieve-all-articles db)))

(defn edit-article [article presenter]
  (let [article-id (:id article)
        new-title (:title article)
        new-content (:content article)]
   (if (empty? new-title)
     (edit-article (assoc article :title (:title (retrieve-article db article-id))) presenter)
     (if (empty? new-content)
       (edit-article (assoc article :content (:content (retrieve-article db article-id))) presenter)
       (do
         (update-article db article-id new-title new-content)
         (presenter {:title new-title
                     :content new-content}))))))

(defn search-for-article [search-word presenter]
  (let [articles (retrieve-all-articles db)]
    (presenter
     (filter #(re-find (re-pattern search-word) (str (:title %) (:content %)))
             (map-indexed (fn [idx article]
                            (assoc article :article-id idx))
                          articles)))))