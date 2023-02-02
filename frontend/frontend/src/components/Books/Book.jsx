import React from 'react';
import { Link } from 'react-router-dom';
import "./BookList.css";

const Book = (book) => {
  return (
    <div className='book-item flex flex-column flex-sb'>
      <div className='book-item-img'>
        <img src = {book.cover} alt = "cover" />
      </div>
      <div className='book-item-info text-center'>
        <Link to = {`/book/${book.id}`} {...book}>
          <div className='book-item-info-item title fw-7 fs-18'>
            <span>{book.title}</span>
          </div>
        </Link>

        <div className='book-item-info-item author fs-15'>
          <span className='text-capitalize fw-7'>Author: </span>
          <span>{book.authors[0].name}</span>
        </div>

        <div className='book-item-info-item edition-count fs-15'>
          <span className='text-capitalize fw-7'>Total downloads: </span>
          <span>{book.download_count}</span>
        </div>

        {/* <div className='book-item-info-item publish-year fs-15'>
          <span className='text-capitalize fw-7'> </span>
          <span>{book.first_publish_year}</span>
        </div> */}
      </div>
    </div>
  )
}

export default Book