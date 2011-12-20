(ns home-sweet-home.web)

(defn present-contact-information [{:keys [name street city phone email]}]
  (str "<html><title>Impressum</title><body>"
         "<h1>Impressum</h1><br />"
         name "<br />"
         street "<br />"
         city "<br />"
         phone "<br />"
         email "<br />"
         "</body></html>"))

(defn present-blog [{:keys [title content]}]
  (str "<html><title>Blog</title><body>"
         "<h1>Blog</h1><br />"
         "<h3>" title "</h3><br />"
         "<p>" content "</p>"
         "</body></html>"))

(defn present-save [res]
  (if (:success res)
      (str "<html><title>Save</title><body>"
           "<h1>Save</h1><br />"
           "<h3>saved successfully")
      (str "error")))
