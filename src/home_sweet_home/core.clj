(ns home-sweet-home.core)

(def blog (atom []))
(def backup (atom []))

(defprotocol Interactor
  (execute [this]))

(deftype ContactInteractor [presenter]
  Interactor
  (execute [this] (.present presenter
                            {:name "Jürgen Bickert"
                             :street "Grafenspitz 11"
                             :city "94099 Ruhstorf"
                             :phone "08531/249164"
                             :email "juergenbickert@gmail.com"})))

(deftype BlogInteractor [article presenter]
  Interactor
  (execute [this] (.present presenter
                           (when (and (>= article 0) (< article (.length @blog)))
                             (@blog article)))))

(deftype ClearBlogInteractor []
  Interactor
  (execute [this] (do (swap! backup concat @blog)
                      (reset! blog []))))

(deftype SaveArticleInteractor [title content presenter]
  Interactor
  (execute [this] (do (swap! blog conj
                          {:title title
                           :content content})
                      (.present presenter {:success true}))))