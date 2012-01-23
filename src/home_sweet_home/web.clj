(ns home-sweet-home.web
  (:use [hiccup core form-helpers])
  (:use [ring.util response]))

(defn default-page [{:keys [title text]}]
  { :status 200
   :headers {}
   :body
   (html [:html
          [:title title]
          [:body text]])})

(defn link-to-article [id text]
  (html [:a {:href (str "article?id=" id)} text]))

(defn present-request-information [req]
  {:status 200
   :headers {"Content-type" "text/plain"}
   :body (str req)})

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

(defn present-save [{:keys [success]}]
  (default-page
    {:title "Save"
     :text
     (if success
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
                                  (html (link-to-article idx (:title article)) [:br]))
                                res))))}))

(defn present-index-page [res]
  (default-page
    {:title "Index"
     :text
     (str (apply str (map (fn [[title link]]
                            (html [:a {:href link} title]
                                  [:br]))
                          res)))}))

(defn present-edit-article [{:keys [id title content]}]
  (default-page
    {:title "Edit Article"
     :text
     (html
      [:h1 "Edit Article"]
      (form-to [:post "/edit"]
               (hidden-field "id" id)
               (text-field "new-title" title) [:br]
               (text-area  "new-content" content) [:br]
               (submit-button "Update")))}))

(defn present-create-article [res]
  (default-page
    {:title ""
     :text
     (html
      [:h1 "Create Article"]
      (form-to [:post "/create"]
               (text-field "title") [:br]
               (text-area "content") [:br]
               (submit-button "Create")))}))

(defn redirect-to-article [{:keys [id]}]
  (redirect (str "/article?id=" id)))