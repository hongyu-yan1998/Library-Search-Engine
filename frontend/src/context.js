import React, {useState, useContext, useEffect} from 'react';
import { useCallback } from 'react';
import coverImg from "./images/cover_not_found.jpg";
import axios from 'axios'

const URL = "http://localhost:8080/books/search/";
const AppContext = React.createContext();

const AppProvider = ({children}) => {
    const [searchTerm, setSearchTerm] = useState("the lost world");
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [resultTitle, setResultTitle] = useState("");

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

    const getBook = ()=>{
        axios.get(`${URL}${searchTerm}`).then(
            response => {
                const docs = response.data;

                if(docs){
                    const newBooks = docs.slice(0, 20).map((bookSingle) => {
                        //console.log(bookSingle);
                        const {id, authors, image, download_count, title, content} = bookSingle;
                        return {
                            id: id,
                            authors: isEmpty(authors) ? authors : "Anonymat",
                            cover: image ? image : coverImg,
                            download_count: download_count,
                            title: title,
                            content_link: content ? content : "",
                        }
                    });

                    setBooks(newBooks);

                    if(newBooks.length > 1){
                        setResultTitle("Your Search Result");
                    } else {
                        setResultTitle("No Search Result Found!")
                    }

                } else {
                    setBooks([]);
                    setResultTitle("No Search Result Found!");
                }
                setLoading(false);
            },
                error => {console.log('failed',error);
        })
    } 

    const fetchBooks = useCallback(async() => {
        setLoading(true);
        try{
            getBook();
        } catch(error){
            console.log(error);
            setLoading(false);
        }
    }, [searchTerm]);

    useEffect(() => {
        fetchBooks();
    }, [searchTerm, fetchBooks]);

    return (
        <AppContext.Provider value = {{
            loading, books, setSearchTerm, resultTitle, setResultTitle,
        }}>
            {children}
        </AppContext.Provider>
    )
}

export const useGlobalContext = () => {
    return useContext(AppContext);
}

export {AppContext, AppProvider};
