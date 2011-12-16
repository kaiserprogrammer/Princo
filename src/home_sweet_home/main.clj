(ns home-sweet-home.main
  (:import [home_sweet_home.core Interactor ContactInteractor BlogInteractor SaveArticleInteractor])
  (:import [home_sweet_home.web Controller StandardController ContactPresenter BlogPresenter])
  (:use ring.adapter.jetty)
  (:use [ring.middleware reload stacktrace params]))

(defn handler [req]
  (if (= (:uri req) "/impressum")
    {:status 200
     :headers {}
     :body (.execute (StandardController. (ContactInteractor. (ContactPresenter.))))}
    (if (= (:uri req) "/blog")
      {:status 200
       :headers {}
       :body (.execute (StandardController. (BlogInteractor.
                                             (Integer/parseInt
                                              (get (:params req) "article"))
                                             (BlogPresenter.))))}
      (if (= (:uri req) "/save")
        {:status 200
         :headers {}
         :body (.execute (StandardController. (SaveArticleInteractor.
                                               (get (:params req) "title")
                                               (get (:params req) "content"))))}
       {:status 200
        :headers {"Content-type" "text/plain"}
        :body (str req)}))))

(def app
  (-> #'handler
      (wrap-params)
      (wrap-reload '[home-sweet-home.main])
      (wrap-stacktrace)))
