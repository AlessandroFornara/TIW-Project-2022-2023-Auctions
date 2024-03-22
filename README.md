# TIW-Project-2022-2023-Auctions
This project is the final test of the [Informatic Technologies for the Web 1](https://www4.ceda.polimi.it/manifesti/manifesti/controller/ManifestoPublic.do?EVN_DETTAGLIO_RIGA_MANIFESTO=evento&aa=2023&k_cf=225&k_corso_la=358&k_indir=I3I&codDescr=085879&lang=EN&semestre=2&idGruppo=4752&idRiga=296972) course at the Polytechnic of Milan, A.Y. 2022/23.

## Team Composition
- [Alessandro Fornara](https://github.com/AlessandroFornara)
- [Andrea Ferrini](https://github.com/AndreaFerrini3)

## Project Specification
The project involves creating two web applications: the first is built using only HTML, and the second is a RIA version of the first. Both versions utilize the same database structure and entries, with the backend powered by Java servlets.

This web application is designed for managing online auctions. Upon logging in, users can navigate to sections dedicated to selling or purchasing items.

In the selling section, users can list new items, complete with detailed descriptions, images, and prices, as well as initiate auctions for these items. Each auction aggregates the prices of the selected items and sets parameters such as the minimum bid increment and the auction's closing time.

On the purchasing side, the application offers a dynamic search feature, enabling users to find auctions that match specific keywords. The search results display open auctions, sorted by the time remaining until each auction closes. Detailed views of each auction provide information such as history of bids in reverse chronological order, and allow for the submission of new bids that must exceed the current highest bid by at least the set minimum increment.

Finally, the application includes a feature for users to view their successful bids, detailing the items won and the final price paid.

You can find the full specification [here](https://github.com/AlessandroFornara/TIW-Project-2022-2023-Auctions/blob/main/Specs) (Exercise 1).

## Software used
- **Eclipse IDE** - Backend and frontend IDE
- **WebSequenceDiagrams** - sequence diagrams
