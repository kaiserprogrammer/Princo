(ns home-sweet-home.core)

(def blog (atom []))
(def backup (atom []))

(defn save-article [title content presenter]
  (do (swap! blog conj
             {:title title
              :content content})
      (presenter {:success true})))

(defn get-article [article presenter]
  (presenter
   (when (and (>= article 0) (< article (.length @blog)))
     (@blog article))))

(defn clear-blog []
  (do (swap! backup concat @blog)
      (reset! blog [])))

(defn get-contact-information [presenter]
  (presenter
   {:name "Jürgen Bickert"
    :street "Grafenspitz 11"
    :city "94099 Ruhstorf"
    :phone "08531/249164"
    :email "juergenbickert@gmail.com"}))

