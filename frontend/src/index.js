import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
// import App from './App';
import { AppProvider } from './context';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './pages/Home/Home';
import About from "./pages/About/About";
import BookList from "./components/Books/BookList";
import BookDetails from "./components/Books/BookDetails";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <AppProvider>
        <BrowserRouter>
            <Routes>
                <Route path = "/" element = {<Home />}>
                <Route path = "about" element = {<About />} />
                <Route path = "book" element = {<BookList />} />
                <Route path = "/book/:thisId" element = {<BookDetails />} />
                </Route>
            </Routes>
        </BrowserRouter> 
    </AppProvider>
   

);
