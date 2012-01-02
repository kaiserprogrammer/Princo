(ns home-sweet-home.core
  (:use home-sweet-home.gateway))

(def db (InMemoryDB. (atom [])))

(defn save-article [article presenter]
  (let [title (:title article)
        content (:content article)]
   (do (.create-article db title content)
       (presenter {:title title
                   :content content
                   :success true}))))

(defn get-article [article-id presenter]
  (presenter
   (if-let [article (.retrieve-article db article-id)]
     (assoc article :id article-id)
     {:id (.count-articles db)
      :title ""
      :content ""})))

(defn get-contact-information [presenter]
  (presenter
   {:name "Jürgen Bickert"
    :street "Grafenspitz 11"
    :city "94099 Ruhstorf"
    :phone "08531/249164"
    :email "juergenbickert@gmail.com"}))

(defn list-all-articles [presenter]
  (presenter (.retrieve-all-articles db)))

(defn edit-article [article presenter]
  (let [article-id (:id article)
        new-title (:title article)
        new-content (:content article)]
    (if-let [current-article (.retrieve-article db article-id)]
      (if (empty? new-title)
        (edit-article (assoc article :title (:title current-article)) presenter)
        (if (empty? new-content)
          (edit-article (assoc article :content (:content current-article)) presenter)
          (do
            (.update-article db article-id new-title new-content)
            (presenter {:title new-title
                        :content new-content}))))
      (save-article article presenter))))

(defn search-for-article [search-word presenter]
  (let [articles (.retrieve-all-articles db)]
    (presenter
     (filter #(re-find (re-pattern search-word) (str (:title %) (:content %)))
             (map-indexed (fn [idx article]
                            (assoc article :article-id idx))
                          articles)))))