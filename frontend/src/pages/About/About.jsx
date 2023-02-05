import React from 'react';
import "./About.css";
import aboutImg from "../../images/about-img.jpg";

const About = () => {
    return (
      <section className='about'>
        <div className='container'>
          <div className='section-title'>
            <h2>About</h2>
          </div>
  
          <div className='about-content grid'>
            <div className='about-img'>
              <img src = {aboutImg} alt = "" />
            </div>
            <div className='about-text'>
              <h2 className='about-title fs-26 ls-1'>About BookHub</h2>
              <p className='fs-17'>
                Le but de ce projet est de créer un moteur de recherche pour une bibliothèque sous la forme d'une application web.
                Ce moteur de recherche aide l'utilisateur à trouver le bon document sur la base de mots-clés ou sur l'expressions régulières. Il suggère également des documents en fonction de l'historique de recherche de l'utilisateur. A l'aide du site https://www.gutenberg.org/ qui stocke des dizaines de milliers de documents dans différents formats, nous récupérons des livres dans notre moteur de recherche.
                La bibliothèque contient donc au moins 1664 livres et chaque livre contient au moins 10^4 mots. </p>
            </div>
          </div>
        </div>
      </section>
    )
  }
  
  export default About