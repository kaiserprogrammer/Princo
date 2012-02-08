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
    (assoc article :id article-id)))

(defn get-contact-information [db]
  {:name "Jürgen Bickert"
   :street "Grafenspitz 11"
   :city "94099 Ruhstorf"
   :phone "08531/249164"
   :email "juergenbickert@gmail.com"})

(defn list-all-articles [db]
  (retrieve-all-articles db))

(defn edit-article [{:keys [id title content]} db]
  (if-let [current-article (retrieve-article db id)]
    (let [new-title (first (remove empty?
                                   [title (:title current-article)]))
          new-content (first (remove empty?
                                     [content (:content current-article)]))]
      (do
        (update-article db id new-title new-content)
        {:id id
         :title new-title
         :content new-content}))))

(defn search-for-article [search-word db]
  (let [articles (retrieve-all-articles db)]
    (filter #(re-find (re-pattern search-word) (str (:title %) (:content %)))
            (map-indexed (fn [idx article]
                           (assoc article :article-id idx))
                         articles))))