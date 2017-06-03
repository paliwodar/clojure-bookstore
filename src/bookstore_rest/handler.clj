(ns bookstore-rest.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [bookstore-rest.db :refer :all]))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn read-book-by-id [id]
  (let [results (read-book id)]
    (if (empty? results)
      {:status 404}
      (ring.util.response/response (first results)))))

(defn insert-new-book [body]
  (let [id (uuid)]
    (insert-book (assoc body "id" id))
    (read-book-by-id id)))

(defn update-existing-book [id body]
  (update-book id (assoc body "id" id))
  (read-book-by-id id))

(defroutes app-routes
           (context "/books" []
             (GET "/" [] (read-books))
             (POST "/" {body :body} (insert-new-book body))
             (context "/:id" [id]
               (GET "/" [] (read-book-by-id id))
               (PUT "/" {body :body} (update-existing-book id body))
               (DELETE "/" [] (delete-book id))))
           (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (wrap-json-body)
      (wrap-json-response)))

