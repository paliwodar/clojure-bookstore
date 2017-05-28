(ns bookstore-rest.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [bookstore-rest.db :refer :all]))


(defroutes app-routes
           (context "/books" []
             (GET "/" [] (read-books))
             (POST "/" {body :body} (insert-book body))
             (context "/:id" [id]
               (GET "/" [] (read-book id))
               (PUT "/" {body :body} (update-book id body))
               (DELETE "/" [] (delete-book id))))
           (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (wrap-json-body)
      (wrap-json-response)))

