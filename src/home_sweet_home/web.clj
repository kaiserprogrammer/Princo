(ns home-sweet-home.web
  (:import home_sweet_home.core.Interactor))

(defprotocol Controller
  (execute [this]))

(deftype StandardController [interactor]
    Controller
  (execute [this] (.execute interactor)))

(defprotocol Presenter
  (present [this res]))

(deftype ContactPresenter []
  Presenter
  (present [this {:keys [name street city phone email]}]
    (str "<html><title>Impressum</title><body>"
         "<h1>Impressum</h1><br />"
         name "<br />"
         street "<br />"
         city "<br />"
         phone "<br />"
         email "<br />"
         "</body></html>")))

(deftype BlogPresenter []
  Presenter
  (present [this {:keys [title content]}]
    (str "<html><title>Blog</title><body>"
         "<h1>Blog</h1><br />"
         "<h3>" title "</h3><br />"
         "<p>" content "</p>"
         "</body></html>")))

(deftype SavePresenter []
  Presenter
  (present [this resp]
    (if (:success resp)
      (str "<html><title>Save</title><body>"
           "<h1>Save</h1><br />"
           "<h3>saved successfully")
      (str "error"))))