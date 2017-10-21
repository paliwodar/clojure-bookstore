(ns bookstore-rest.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [bookstore-rest.handler :refer :all]
            [bookstore-rest.db :as db]
            [cheshire.core :refer [parse-string]]))

(deftest test-app
  (let [books-data (ref ())]
    (with-redefs
      [db/read-books (fn [] @books-data)
       db/read-book (fn [id] (filter #(= (% "id") id) @books-data))
       db/insert-book (fn [book] (dosync (alter books-data conj book)))
       db/delete-book (fn [id] (dosync (ref-set books-data (remove #(= (% "id") id) @books-data))))
       db/update-book (fn [id body] (dosync (ref-set books-data (cons body (remove #(= (% "id") id) @books-data)))))]

      (testing "not-found route"
        (let [response (app (mock/request :get "/"))]
          (is (= (:status response) 404))))

      (testing "bookstore should be initially empty"
        (let [response (app (mock/request :get "/books"))]
          (is (= (:status response) 200))
          (is (= (:body response) "[]"))))

      (testing "a book is inserted"
        (let [insertion-response (-> (mock/request :post "/books")
                                     (mock/body "{\"author\":\"Andrzej Sapkowski\", \"title\":\"Last Wish\"}")
                                     (mock/content-type "application/json")
                                     app)]
          (is (= (:status insertion-response) 200))
          (is (= "Andrzej Sapkowski" ((parse-string (:body insertion-response)) "author")))))

      (testing "there should be exactly one book present after insertion"
        (let [read-response (app (mock/request :get "/books"))
              books (parse-string (:body read-response))
              book (first books)]
          (is (= 1 (count books)))
          (is (= (book "author") "Andrzej Sapkowski"))
          (is (= (book "title") "Last Wish"))
          (is (some? (book "id")))))

      (testing "reading the book by its id should give the expected result"
        (let [book-id ((first @books-data) "id")
              read-response (app (mock/request :get (str "/books/" book-id)))
              book (parse-string (:body read-response))]
          (is (some? book-id))
          (is (some? book))
          (is (= (book "author") "Andrzej Sapkowski"))
          (is (= (book "title") "Last Wish"))))

      (testing "reading book using non-existing id should result in 404"
        (let [read-response (app (mock/request :get "/books/fake-id"))]
          (is (= (read-response :status) 404))))

      (testing "a book is updated"
        (let [book-id ((first @books-data) "id")
              updated-response (-> (mock/request :put (str "/books/" book-id))
                                   (mock/body "{\"author\":\"Andrzej Sapkowski\", \"title\":\"Sword of Destiny\"}")
                                   (mock/content-type "application/json")
                                   app)]
          (is (= (:status updated-response) 200))
          (is (= "Sword of Destiny" ((parse-string (:body updated-response)) "title")))))

      (testing "a book is deleted"
        (let [book-id ((first @books-data) "id")
              delete-response (app (mock/request :delete (str "/books/" book-id)))]
          (is (= (:status delete-response) 200))))

      (testing "bookstore should be empty after deleting the only book"
        (let [response (app (mock/request :get "/books"))]
          (is (= (:status response) 200))
          (is (= (:body response) "[]")))))))





