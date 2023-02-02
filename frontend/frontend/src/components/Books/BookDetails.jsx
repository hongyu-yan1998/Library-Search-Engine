import React, {useState, useEffect} from 'react';
import { useParams } from 'react-router-dom';
import Loading from "../Loader/Loader";
import coverImg from "../../images/cover_not_found.jpg";
import "./BookDetails.css";
import {FaArrowLeft} from "react-icons/fa";
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios'

const URL = "http://localhost:8080/books/";

const BookDetails = () => {
  const {id} = useParams();
  const [loading, setLoading] = useState(false);
  const [book, setBook] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    async function getBookDetails(){
        axios.get(`${URL}${id}`).then(
            response => {
                const docs = response.data;

                if(docs){
                    console.log(docs.authors[0].name);
                    const {bookshelves, title, image, content, authors, subjects} = response.data;
                    const newBook = {
                        description: bookshelves ? bookshelves : "No bookshelves found",
                        title: title,
                        cover_img: image ? image : coverImg,
                        content: content,
                        authors: authors
                        // subject_places: subject_places ? subject_places.join(", ") : "No subject places found",
                        // subject_times : subject_times ? subject_times.join(", ") : "No subject times found",
                        // subjects: subjects ? subjects.join(", ") : "No subjects found"
                    };
                    setBook(newBook);
                } else {
                    setBook(null);
                }
                setLoading(false);
            },
                error => {console.log('failed',error);
                setLoading(false);
        })
    }
    getBookDetails();
  }, [id]);

  if(loading) return <Loading />;

  return (
    <section className='book-details'>
      <div className='container'>
        <button type='button' className='flex flex-c back-btn' onClick={() => navigate("/book")}>
          <FaArrowLeft size = {22} />
          <span className='fs-18 fw-6'>Go Back</span>
        </button>

        <div className='book-details-content grid'>
          <div className='book-details-img'>
            <img src = {book?.cover_img} alt = "cover img" />
          </div>
          <div className='book-details-info'>
            <div className='book-details-item title'>
              <span className='fw-6 fs-24'>{book?.title}</span>
            </div>
            <div className='book-details-item'>
                <h4>Authors:</h4>
                <ul>
                    {book?.authors.map((user) => (
                    <li key={user.name}>{user.name}
                    </li>
                    ))}
                </ul>
            </div>
            <div className='book-details-item description'>
              <span>{book?.description}</span>
            </div>
            <div className='book-details-item'>
              {/* <span><Link to={book?.content}>{book?.content}</Link>
              </span> */}
            </div>
            {/* <div className='book-details-item description'>
              <span>{book?.description}</span>
            </div>
            <div className='book-details-item'>
              <span className='fw-6'>Subject Places: </span>
              <span className='text-italic'>{book?.subject_places}</span>
            </div>
            <div className='book-details-item'>
              <span className='fw-6'>Subject Times: </span>
              <span className='text-italic'>{book?.subject_times}</span>
            </div>
            <div className='book-details-item'>
              <span className='fw-6'>Subjects: </span>
              <span>{book?.subjects}</span>
            </div> */}
          </div>
        </div>
      </div>
    </section>
  )
}

export default BookDetails