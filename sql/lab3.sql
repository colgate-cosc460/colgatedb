-- COSC 460 Fall 2016, Lab 3

-- These set the output format.  Please be sure to leave these settings as is. 
.header OFF
.mode list 

-- For each of the queries below, put your SQL in the place indicated by the comment.  
-- Be sure to have all the requested columns in your answer, in the order they are 
-- listed in the question - and be sure to sort things where the question requires 
-- them to be sorted, and eliminate duplicates where the question requires that.   
-- I will grade the assignment by running the queries on a test database and 
-- eyeballing the SQL queries where necessary.  I won't grade on SQL style, but 
-- I also won't give partial credit for any individual question - so you should be 
-- confident that your query works. At the very least, your output should match 
-- the example output.


-- Q1 -  Find the titles of all movies directed by Steven Spielberg.  
select " ";
select "Q1";
-- Put your SQL for Q1 below 


-- Q2 -  Find all years that have a movie that received a rating of 4 or 5, 
--       and sort them in increasing order.   A given year should not appear 
--       more than once.            
select " ";
select "Q2";
-- Put your SQL for Q2 below 


-- Q3 -  Write a query to return the ratings data in a more 
--       readable format: reviewer name, movie title, stars, and ratingDate. 
--       Also, sort the data, first by reviewer name, then by movie title, 
--       and lastly by number of stars, all in ascending order.
select " ";
select "Q3";
-- Put your SQL for Q3 below 


-- Q4 -  Find the movie ids of all movies that have no ratings.
select " ";
select "Q4";
-- Put your SQL for Q4 below 


-- Q5 -  Find the movie ids of all movies that have not received a rating of
--       at least 4 stars.  Your result should include movies that have not
--       received any ratings.
select " ";
select "Q5";
-- Put your SQL for Q5 below 


-- Q6 -  Find ids and titles of movies that have been reviewed by both Elizabeth Thomas
--       and James Cameron.
select " ";
select "Q6";
-- Put your SQL for Q6 below 


-- Q7 -  Find names of reviewers who have reviewed movies from before 1960
--       or whose titles start with the letter A
select " ";
select "Q7";
-- Put your SQL for Q7 below 


-- Q8 -  Find titles of movies directed by a director with a first name of George 
--       or receiving >= 4 stars by some reviewer.
select " ";
select "Q8";
-- Put your SQL for Q8 below 


-- Q9 -  Find pairs consisting of a reviewer id and a movie id corresponding 
--       to situations where the reviewer reviewed the movie at least twice.
select " ";
select "Q9";
-- Put your SQL for Q9 below 


-- Q10 -  For all cases where the same reviewer rated the same movie more than
--       once and gave it a higher rating in a subsequent review, return the 
--       reviewer's name, the title of the movie, the date of the earlier review,
--       the date of the subequent review and the number of *additional* stars the 
--       movie received in the subsequent review.  Order the results by movie title.
--
--       (Note that if a reviewer reviewed a movie three times, each time with 
--        a higher rating, then your result would include three rows for this 
--        movie and reviewer pair, corresponding to the comparison of the 1st to 2nd 
--        review, the 2nd to 3rd review, and finally the 1st to 3rd review.)
select " ";
select "Q10";
-- Put your SQL for Q10 below 


