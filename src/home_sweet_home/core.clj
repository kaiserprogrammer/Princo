(ns home-sweet-home.core
  (:use home-sweet-home.gateway)
  (:import [home-sweet-home.gateway.EntityGateway]))

(defn save-article [article db]
  (let [title (:title article)
        content (:content article)]
   (do (create-article db title content)
       {:title title
        :content content
        :success true})))

(defn get-article [article-id db]
  (if-let [article (retrieve-article db article-id)]
    (assoc article :id article-id)
    {:id (count-articles db)
     :title ""
     :content ""}))

(defn get-contact-information [db]
  {:name "Jürgen Bickert"
   :street "Grafenspitz 11"
   :city "94099 Ruhstorf"
   :phone "08531/249164"
   :email "juergenbickert@gmail.com"})

(defn list-all-articles [db]
  (retrieve-all-articles db))

(defn edit-article [article db]
  (let [article-id (:id article)
        new-title (:title article)
        new-content (:content article)]
    (if-let [current-article (retrieve-article db article-id)]
      (if (empty? new-title)
        (edit-article (assoc article :title (:title current-article)) db)
        (if (empty? new-content)
          (edit-article (assoc article :content (:content current-article)) db)
          (do
            (update-article db article-id new-title new-content)
            {:title new-title
             :content new-content})))
      (save-article article db))))

(defn search-for-article [search-word db]
  (let [articles (retrieve-all-articles db)]
    (filter #(re-find (re-pattern search-word) (str (:title %) (:content %)))
            (map-indexed (fn [idx article]
                           (assoc article :article-id idx))
                         articles))))