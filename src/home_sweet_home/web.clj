(ns home-sweet-home.web
  (:use [hiccup core form-helpers]))

(defn default-page [{:keys [title text]}]
  (html [:html
         [:title title]
         [:body text]]))

(defn link-to-article [id text]
  (str "<a href=\"article?id=" id "\">" text "</a>"))

(defn present-contact-information [{:keys [name street city phone email]}]
  (default-page
    {:title "Impressum"
     :text
     (html
      [:h1 "Impressum" [:br]]
      name [:br]
      street [:br]
      city [:br]
      phone [:br]
      email [:br])}))

(defn present-blog [{:keys [title content]}]
  (default-page
    {:title "Blog"
     :text
     (html
      [:h1 "Blog" [:br]]
      [:h3 title] [:br]
      [:p content])}))

(defn present-save [res]
  (default-page
    {:title "Save"
     :text
     (if (:success res)
       (html
        [:h1 "Save" [:br]]
        [:h3 "saved successfully"])
       (str "error"))}))

(defn present-all-articles [res]
  (default-page
    {:title "Articles"
     :text
     (html
      [:h1 "Articles" [:br]]
      (if (empty? res)
        [:h3 "No articles yet."]
        (apply str (map-indexed (fn [idx article]
                                  (link-to-article idx (:title article)))
                                res))))}))

(defn present-index-page [res]
  (default-page
    {:title "Index"
     :text
     (str (apply str (map (fn [[title link]]
                            (html [:a {:href link} title]))
                          res)))}))

(defn present-edit-article [res]
  (default-page
    {:title "Edit Article"
     :text
     (html
      [:h1 "Edit Article"]
      (form-to [:post "/edit"]
               (hidden-field "id" (:id res))
               (text-field "new-title" (:title res)) [:br]
               (text-area  "new-content" (:content res)) [:br]
               (submit-button "Update")))}))