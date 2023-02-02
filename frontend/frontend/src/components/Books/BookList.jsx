import React from 'react';
import { useGlobalContext } from '../../context';
import Book from "../Books/Book";
import Loading from "../Loader/Loader";
import coverImg from "../../images/cover_not_found.jpg";
import "./BookList.css";

//https://www.gutenberg.org/files/6761/6761-0.txt
//https://www.gutenberg.org/cache/epub/2160/pg2160.cover.medium.jpg


const BookList = () => {
  const {books, loading, resultTitle} = useGlobalContext();
  const booksWithCovers = books.map((singleBook) => {
    console.log(singleBook.authors);
    return {
      ...singleBook,
      // removing /works/ to get only id
      id: singleBook.id,
      cover_img: singleBook.cover ? singleBook.cover : coverImg
    }
  });

  if(loading) return <Loading />;

  return (
    <section className='booklist'>
      <div className='container'>
        <div className='section-title'>
          <h2>{resultTitle}</h2>
        </div>
        <div className='booklist-content grid'>
          {
            booksWithCovers.slice(0, 30).map((item, index) => {
              return (
                <Book key = {index} {...item} />
              )
            })
          }
        </div>
      </div>
    </section>
  )
}

export default BookList