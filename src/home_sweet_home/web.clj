(ns home-sweet-home.web)

(defn default-page [{:keys [title text]}]
  (str "<html><title>" title "</title><body>"
       text
       "</body></html>"))

(defn link-to-article [id text]
  (str "<a href=\"blog?article=" id "\">" text "</a>"))

(defn present-contact-information [{:keys [name street city phone email]}]
  (default-page
    {:title "Impressum"
     :text
     (str "<h1>Impressum</h1><br />"
          name "<br />"
          street "<br />"
          city "<br />"
          phone "<br />"
          email "<br />")}))

(defn present-blog [{:keys [title content]}]
  (default-page
    {:title "Blog"
     :text
     (str "<h1>Blog</h1><br />"
          "<h3>" title "</h3><br />"
          "<p>" content "</p>")}))

(defn present-save [res]
  (default-page
    {:title "Save"
     :text
     (if (:success res)
       (str "<h1>Save</h1><br />"
            "<h3>saved successfully")
       (str "error"))}))

(defn present-all-articles [res]
  (default-page
    {:title "Articles"
     :text
     (str
      "<h1>Articles</h1><br />"
      (if (empty? res)
        "<h3>No articles yet.</h3>"
        (apply str (map-indexed (fn [idx article]
                                  (link-to-article idx (:title article)))
                                res))))}))

(defn present-index-page [res]
  (default-page
    {:title "Index"
     :text
     (str (apply str (map (fn [[title link]]
                            (str "<a href=\"" link "\">" title "</a>"))
                          res)))}))

(defn present-edit-article [res]
  (default-page
    {:title "Edit Article"
     :text
     (str "<h1>Edit Article</h1>"
          "<form action=\"edit\" method=\"post\">"
          "<input name=\"new-title\" type=\"textfield\" value=\"" (:title res) "\" /><br />"
          "<textarea name=\"new-content\" cols=\"20\" rows=\"5\">" (:content res) "</textarea><br />"
          "<input type=\"submit\" value=\"Update\" />"
          "</form>")}))