import React, {useState, useEffect} from 'react';
import { useParams } from 'react-router-dom';
import Loading from "../Loader/Loader";
import coverImg from "../../images/cover_not_found.jpg";
import "./BookDetails.css";
import {FaArrowLeft} from "react-icons/fa";
import { useNavigate, Link } from 'react-router-dom';
import Book from "./Book";
import axios from 'axios'

const URL = "http://localhost:8080/books/";

const isEmpty = (strIn) =>
{
    if (strIn === undefined)
    {
        return true;
    }
    else if(strIn == null)
    {
        return true;
    }
    else if(strIn == "")
    {
        return true;
    }
    else
    {
        return false;
    }
}

const BookDetails = () => {
  const {thisId} = useParams();
  const [loading, setLoading] = useState(false);
  const [book, setBook] = useState(null);
  const [recommand, setRecommandBooks] = useState([]);
  const [recommandTitle, setTitle] = useState("");
  const navigate = useNavigate();

  async function getBookDetails(){
    axios.get(`${URL}${thisId}`).then(
        response => {
            const docs = response.data;

            if(docs){
                const {bookshelves, title, image, content, authors, subjects, languages} = response.data;
                const newBook = {
                    description: bookshelves ? bookshelves : "No bookshelves found",
                    title: title,
                    cover_img: image ? image : coverImg,
                    content: isEmpty(content) ? "No link exist" : <span><Link to={content} className="link">link to the content</Link></span>,
                    authors: authors ? authors : "Anonymat",
                    languages: languages ? languages.join(", ") : "languages",
                    subjects: subjects ? subjects.join(", ") : "No subjects found"
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

  async function getRecommandList(){
    axios.get(`${URL}suggestion/${thisId}`).then(
      response => {
          const docs = response.data;
          if(docs){
              const newBooks = docs.slice(0, 20).map((bookSingle) => {
                const {id, bookshelves, title, image, content, authors, subjects, languages, download_count} = bookSingle;
                console.log(bookSingle)
                return {
                    id:id,
                    description: bookshelves ? bookshelves : "No bookshelves found",
                    title: title,
                    cover_img: image,
                    content: content,
                    authors: isEmpty(authors) ? "Anonymat" : authors[0].name,
                    languages: languages ? languages.join(", ") : "languages",
                    subjects: subjects ? subjects.join(", ") : "No subjects found",
                    download_count: download_count ? download_count : ""
                }
              });
              setRecommandBooks(newBooks);

              if(newBooks.length > 1){
                setTitle("Similar Books");
              } else {
                setTitle("No Similar books!")
              }
          } else {
            setRecommandBooks([]);
            setTitle("No Similar books!");
          }
          setLoading(false);
      },
          error => {console.log('failed',error);
          setLoading(false);
      })
  }

  useEffect(() => {
    setLoading(true);
    getBookDetails();
    getRecommandList();
  }, [thisId]);

  const booksWithCovers = recommand.map((singleBook) => {
    return {
      ...singleBook,
      cover: singleBook.cover_img ? singleBook.cover_img : coverImg,
      name: singleBook.authors ? singleBook.authors : "Anonymous",
      content: isEmpty(singleBook.content) ? "No link exist" : <span><Link to={singleBook.content} className="link">link to the content</Link></span>
    }
  });

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
              <span className='fw-6'>Languages: </span>
              <span>{book?.languages}</span>
            </div>
            <div className='book-details-item'>
              <span className='fw-6'>Subjects: </span>
              <span>{book?.subjects}</span>
            </div>

            <div className='book-details-item'>
              {book?.content}
            </div>
          </div>
        </div>

        <section className='booklist'>
          <div className='container'>
            <div className='section-title'>
              <h2>{recommandTitle}</h2>
            </div>
                <div className='booklist-content grid'>
                  {
                    booksWithCovers?.slice(0, 10).map((item, index) => {
                      return (
                        <Book key = {index} {...item} />
                      )
                    })
                  }
                </div>
          </div>
        </section>
      </div>
    </section>
  )
}

export default BookDetails