export var buyOrchestrator = new BuyOrchestrator;
import { updateEventsLocalStorage } from './LastEventObject.js';
import { updateLastClickedAuctionsLocalStorage } from './LastEventObject.js';

function BuyOrchestrator() {


	this.createKeywordForm = function() {

		document.getElementById('search-message').textContent = 'Use this form to search for what you\'re looking for';

		let formSearchByKeyword = document.createElement('form');
		formSearchByKeyword.id = 'formSearchByKeyword';
		formSearchByKeyword.action = '#';

		let input = document.createElement('input');
		input.type = 'text';
		input.name = 'keyword';
		input.placeholder = 'Keyword';
		input.required;

		let searchButton = document.createElement('input');
		searchButton.id = 'searchButton';
		searchButton.type = 'submit';
		searchButton.value = 'Search';

		formSearchByKeyword.appendChild(input);
		formSearchByKeyword.appendChild(searchButton);

		document.getElementById('formSearchByKeyword').appendChild(formSearchByKeyword);

		searchButton.addEventListener('click', (e) => {
			e.preventDefault();
			var form = e.target.closest("form");
			if (form.checkValidity()) {
				makeCall("POST", 'GoToBuy', e.target.closest("form"),
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							var message = x.responseText;
							switch (x.status) {
								case 200:
									var fromServlet = JSON.parse(message);

									document.getElementById("errormessage").textContent = 'Everything is good here';
									document.getElementById("erroralert").innerHTML = "";

									let foundAuctionsByKeywordMessage = document.getElementById('foundAuctionsByKeywordMessage');
									let keyword = fromServlet.keyword;

									foundAuctionsByKeywordMessage.innerHTML = "";

									if (fromServlet.foundAuctions != null) {
										foundAuctionsByKeywordMessage.textContent = 'Here is a list of auctions that contain ' + keyword + ' in the name or description of an article';
										buyOrchestrator.refreshFoundAuctions(fromServlet);
									}
									else {
										buyOrchestrator.refreshFoundAuctions(fromServlet);
									}

									break;
								case 401: // unauthorized
									document.getElementById("errormessage").textContent = message;
									document.getElementById("erroralert").textContent = 'ERROR';
									break;
								case 502: // bad gateway
									document.getElementById("errormessage").textContent = message;
									document.getElementById("erroralert").textContent = 'ERROR';
									break;
							}
						}
					}
				);
			} else {
				form.reportValidity();
			}
		}


		)
	}

	/**
	 * when an user places an offer, this method will refresh only the table with the offers
	 *  */
	this.generateOffersTable = function(message) {

		document.getElementById('table2Section').innerHTML = "";;

		let table2 = document.createElement('table');

		let table2head = table2.createTHead();
		let table2headRow = table2head.insertRow();
		table2headRow.insertCell().textContent = 'Bidder';
		table2headRow.insertCell().textContent = 'Date and Time';
		table2headRow.insertCell().textContent = 'Bid Value';

		let table2body = table2.createTBody();

		let auctionOffers = message.auctionOffers;

		if (auctionOffers != null) {
			auctionOffers.forEach(function(offer) {

				let row1 = table2body.insertRow();
				row1.insertCell().textContent = offer.bidder;
				row1.insertCell().textContent = offer.dateTime;
				row1.insertCell().textContent = offer.offerPrice;

			})

			document.getElementById('table2Section').appendChild(table2);
		}
		else {
			document.getElementById('badOfferMessage').innerHTML = "";
			document.getElementById('badOfferMessage').textContent = 'there aren\'t offers for this auction';
		}

	}

	this.updateOffersTable = function(message) {

		var section = document.getElementById('table2Section');

		if (section) {
			console.log('section ok!');
			var table = section.getElementsByTagName('table')[0];

			if (table) {
				console.log('table ok!');
				var tbody = table.getElementsByTagName('tbody')[0];

				if (tbody) {
					console.log('tbody ok!');
					var newRow = tbody.insertRow(0);

					let offer = message.offer;
					console.log(offer);

					if (offer != null) {
						newRow.insertCell().textContent = offer.bidder;
						newRow.insertCell().textContent = offer.dateTime;
						newRow.insertCell().textContent = offer.offerPrice;
						console.log(offer.bidder);
						console.log(offer.dateTime);
						console.log(offer.offerPrice);
					}



				} else { console.log('no body'); }
			} else {

				let table2 = document.createElement('table');

				let table2head = table2.createTHead();
				let table2headRow = table2head.insertRow();
				table2headRow.insertCell().textContent = 'Bidder';
				table2headRow.insertCell().textContent = 'Date and Time';
				table2headRow.insertCell().textContent = 'Bid Value';

				let table2body = table2.createTBody();
				let row1 = table2body.insertRow();
				let offer = message.offer;
				row1.insertCell().textContent = offer.bidder;
				row1.insertCell().textContent = offer.dateTime;
				row1.insertCell().textContent = offer.offerPrice;

				document.getElementById('table2Section').appendChild(table2);
			}
		} else { console.log('no section'); }

	}


	/**
	 * refreshes view offers
	 */

	this.refreshViewOffers = function(message) {

		document.getElementById('pageTitle').innerHTML = "";
		document.getElementById('welcome-message').innerHTML = "";
		document.getElementById('minimum-raise-message').innerHTML = "";

		document.getElementById('pageTitle').textContent = 'BIDS';
		document.getElementById('welcome-message').textContent = 'Here are the offers and data of the articles for auction ' + message.auction.auctionId + ' by user ' + message.auction.username;
		document.getElementById('minimum-raise-message').textContent = 'The minimum bid increment is ' + message.auction.minimumRaise;

		this.clearViewOffers();

		//prima tabella
		let table1 = document.createElement('table');

		let table1head = table1.createTHead();
		let table1headRow = table1head.insertRow();
		table1headRow.insertCell().textContent = 'Code';
		table1headRow.insertCell().textContent = 'Name';
		table1headRow.insertCell().textContent = 'Description';
		table1headRow.insertCell().textContent = 'Price';

		let table1body = table1.createTBody();

		let articles = message.auction.articles;
		articles.forEach(function(article) {

			let row1 = table1body.insertRow();
			row1.insertCell().textContent = article.code;
			row1.insertCell().textContent = article.name;
			row1.insertCell().textContent = article.description;
			row1.insertCell().textContent = article.price;
		})

		//form per aggiungere offerte
		let addOfferForm = document.createElement('form');
		addOfferForm.id = 'addOfferForm';
		addOfferForm.action = '#';

		let input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'auctionID';
		input.value = message.auction.auctionId;

		let input2 = document.createElement('input');
		input2.type = 'hidden';
		input2.name = 'bidderUsername';
		input2.value = message.username;

		let input3 = document.createElement('input');
		input3.type = 'text';
		input3.name = 'offer';
		input3.placeholder = 'Offer: 00';
		input.pattern = '[0-9]+?';

		let button = document.createElement('input');
		button.id = 'button';
		button.type = 'submit';
		button.value = 'Place Bid';


		button.addEventListener('click', (e) => {
			e.preventDefault();
			var closestForm = e.target.closest("form");
			if (addOfferForm.checkValidity()) {
				makeCall("POST", 'AddOffer', closestForm,
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							var message = x.responseText;
							switch (x.status) {
								case 200:
									var fromServlet = JSON.parse(message);

									console.log('ADD OFFER');

									document.getElementById("errormessage").textContent = 'Everything is good here';
									document.getElementById("erroralert").innerHTML = "";

									let done = fromServlet.done;
									if (done) {
										document.getElementById('badOfferMessage').innerHTML = "";

										console.log('if ok!');

										buyOrchestrator.updateOffersTable(fromServlet);
									}
									else {
										document.getElementById('badOfferMessage').textContent = 'The bid was not successful, please try again';
									}

									var currentDate = new Date();
									var expirationDate = new Date(currentDate);
									expirationDate.setDate(currentDate.getDate() + 30);

									updateEventsLocalStorage(sessionStorage.getItem('username'), 'addOffer', expirationDate);

									break;

								// cambiare gli errori

								case 400: // bad request
									document.getElementById("errormessage").textContent = message;
									document.getElementById("erroralert").textContent = 'ERROR';
									break;
								case 502: // bad gateway
									document.getElementById("errormessage").textContent = message;
									document.getElementById("erroralert").textContent = 'ERROR';
									break;
							}
						}
					}
				);
			} else {
				form.reportValidity();
			}


		})


		addOfferForm.appendChild(input);
		addOfferForm.appendChild(input2);
		addOfferForm.appendChild(input3);
		addOfferForm.appendChild(button);

		// seconda tabella
		buyOrchestrator.generateOffersTable(message);

		document.getElementById('table1Section').appendChild(table1);
		document.getElementById('viewOffersSection').appendChild(addOfferForm);
	}

	this.refreshWonAuctions = function(message) {

		let table1 = document.createElement('table');

		let table1head = table1.createTHead();
		let table1headRow = table1head.insertRow();
		table1headRow.insertCell().textContent = 'Auction ID';
		table1headRow.insertCell().textContent = 'Final Price';

		let table1body = table1.createTBody();

		let wonAuctions = message.wonAuctions;
		wonAuctions.forEach(function(auction) {

			let row1 = table1body.insertRow();
			row1.insertCell().textContent = auction.auctionId;
			row1.insertCell().textContent = auction.finalPrice;

			let row2 = table1body.insertRow(); // fare append della seconda tabella qui sopra

			let table2 = document.createElement('table');

			let table2head = table2.createTHead();
			let table2headRow = table2head.insertRow();
			table2headRow.insertCell().textContent = 'Name';
			table2headRow.insertCell().textContent = 'Code';
			table2headRow.insertCell().textContent = 'Description';

			let table2body = table2.createTBody();

			let articles = auction.articles;
			articles.forEach(function(article) {
				let row = table2.insertRow();
				row.insertCell().textContent = article.name;
				row.insertCell().textContent = article.code;
				row.insertCell().textContent = article.description;
			});

			row2.appendChild(table2);
		})

		document.getElementById('wonAuctionsSection').appendChild(table1);
	}

	this.refreshLastClickedAuctions = function(message) {

		this.clearLastClickedAuctions;

		let mainTable = document.createElement('table');

		let mainTableHead = mainTable.createTHead();

		let mainTableHeaderRow = mainTableHead.insertRow();
		mainTableHeaderRow.insertCell().textContent = 'Auction ID';
		mainTableHeaderRow.insertCell().textContent = 'Auction Details';

		let mainTableBody = mainTable.createTBody();

		let lastClickedAuctions = message;

		if (lastClickedAuctions != null) {
			lastClickedAuctions.forEach(function(auction) {

				let id = auction.auctionId;

				let mainTableRow = mainTableBody.insertRow();
				mainTableRow.insertCell().textContent = auction.auctionId;

				let secondTable = document.createElement('table');

				let secondTableHead = document.createElement('thead');
				let secondTableHeaderRow = secondTableHead.insertRow();
				secondTableHeaderRow.insertCell().textContent = 'Current Offer';
				secondTableHeaderRow.insertCell().textContent = 'End Date';

				secondTable.appendChild(secondTableHead);

				let secondTableBody = document.createElement('tbody');
				let row1 = secondTableBody.insertRow();
				let row2 = secondTableBody.insertRow();

				row1.insertCell().textContent = auction.maxBid;
				row1.insertCell().textContent = auction.endDate;

				let thirdTable = document.createElement('table');
				let thirdTableHead = document.createElement('thead');
				let thirdTableHeaderRow = thirdTableHead.insertRow();
				thirdTableHeaderRow.insertCell().textContent = 'Article Code';
				thirdTableHeaderRow.insertCell().textContent = 'Article Name';

				thirdTable.appendChild(thirdTableHead);

				let thirdTableBody = document.createElement('tbody');
				let auctionArticles = auction.articles;

				auctionArticles.forEach(function(article) {
					let row = thirdTableBody.insertRow();
					row.insertCell().textContent = article.code;
					row.insertCell().textContent = article.name;
				});

				thirdTable.appendChild(thirdTableBody);

				row2.insertCell().appendChild(thirdTable);

				secondTable.appendChild(secondTableBody);

				mainTableRow.insertCell().appendChild(secondTable);

				document.getElementById('lastClickedAuctionsMessage').textContent = 'You clicked on these auctions recently';
				document.getElementById('lastClickedAuctionsSection').appendChild(mainTable);


			});
		}

	}

	this.refreshFoundAuctions = function(message) {

		this.clearFoundAuctions();

		let mainTable = document.createElement('table');

		let mainTableHead = mainTable.createTHead();

		let mainTableHeaderRow = mainTableHead.insertRow();
		mainTableHeaderRow.insertCell().textContent = 'Auction ID';
		mainTableHeaderRow.insertCell().textContent = 'Auction Details';

		let mainTableBody = mainTable.createTBody();

		let foundAuctions = message.foundAuctions;

		if (foundAuctions != null) {
			foundAuctions.forEach(function(auction) {

				let id = auction.auctionId;

				let mainTableRow = mainTableBody.insertRow();
				mainTableRow.insertCell().textContent = auction.auctionId;

				let secondTable = document.createElement('table');

				let secondTableHead = document.createElement('thead');
				let secondTableHeaderRow = secondTableHead.insertRow();
				secondTableHeaderRow.insertCell().textContent = 'Current Offer';
				secondTableHeaderRow.insertCell().textContent = 'Remaining Time';

				secondTable.appendChild(secondTableHead);

				let secondTableBody = document.createElement('tbody');
				let row1 = secondTableBody.insertRow();
				let row2 = secondTableBody.insertRow();

				row1.insertCell().textContent = auction.maxBid;

				let days = Math.floor(auction.remainingTimeMillis / (1000 * 60 * 60 * 24));
				let hours = Math.floor((auction.remainingTimeMillis / (1000 * 60 * 60)) % 24);

				let date = days + " days and " + hours + " hours";
				row1.insertCell().textContent = date;

				let thirdTable = document.createElement('table');
				let thirdTableHead = document.createElement('thead');
				let thirdTableHeaderRow = thirdTableHead.insertRow();
				thirdTableHeaderRow.insertCell().textContent = 'Article Code';
				thirdTableHeaderRow.insertCell().textContent = 'Article Name';

				thirdTable.appendChild(thirdTableHead);

				let thirdTableBody = document.createElement('tbody');
				let auctionArticles = auction.articles;

				let imageAppearCell = document.createElement('td');

				auctionArticles.forEach(function(article) {
					let row = thirdTableBody.insertRow();
					row.insertCell().textContent = article.code;
					row.insertCell().textContent = article.name;

					let imageForm = document.createElement('form');
					imageForm.id = 'imageForm';
					imageForm.action = '#';

					let imageInput = document.createElement('input');
					imageInput.type = 'hidden';
					imageInput.name = 'image';
					imageInput.value = article.image;
					imageForm.appendChild(imageInput);

					let submitImageInput = document.createElement('input');
					submitImageInput.type = 'submit';
					submitImageInput.value = 'image';
					imageForm.appendChild(submitImageInput);

					let imageCell = row.insertCell();

					submitImageInput.addEventListener('click', (e) => {
						e.preventDefault();
						var closestForm = e.target.closest("form");
						if (form.checkValidity()) {
							makeCall("POST", 'OpenImage', closestForm,
								function(x) {
									if (x.readyState == XMLHttpRequest.DONE) {
										var message = x.responseText;
										switch (x.status) {
											case 200:
												var fromServlet = JSON.parse(message);

												console.log(fromServlet);

												document.getElementById("errormessage").textContent = 'Everything is good here';
												document.getElementById("erroralert").innerHTML = "";

												imageAppearCell.innerHTML = "";
												imageAppearCell.innerHTML = "<img src =\"" + fromServlet + "\">";

												break;
											case 400: // bad request
												document.getElementById("errormessage").textContent = message;
												document.getElementById("erroralert").textContent = 'ERROR';
												break;
											case 401: // SC_UNAUTHORIZED
												document.getElementById("errormessage").textContent = message;
												document.getElementById("erroralert").textContent = 'ERROR';
												break;
										}
									}
								}
							);
						} else {
							form.reportValidity();
						}
					})

					imageCell.appendChild(imageForm);
				});

				thirdTable.appendChild(thirdTableBody);

				row2.insertCell().appendChild(thirdTable);

				row2.appendChild(imageAppearCell);

				secondTable.appendChild(secondTableBody);

				mainTableRow.insertCell().appendChild(secondTable);

				let form = document.createElement('form');
				form.action = '#';

				let auctionIdInput = document.createElement('input');
				auctionIdInput.type = 'hidden';
				auctionIdInput.name = 'auctionID';
				auctionIdInput.value = id;
				form.appendChild(auctionIdInput);


				let doneInput = document.createElement('input');
				doneInput.type = 'hidden';
				doneInput.name = 'done';
				doneInput.value = 'true';
				form.appendChild(doneInput);


				let button = document.createElement('input');
				button.id = 'ViewOffersButton';
				button.type = 'submit';
				button.value = 'View Bids';


				button.addEventListener('click', (e) => {
					e.preventDefault();
					var closestForm = e.target.closest("form");
					if (form.checkValidity()) {
						makeCall("POST", 'ViewOffers', closestForm,
							function(x) {
								if (x.readyState == XMLHttpRequest.DONE) {
									var message = x.responseText;
									switch (x.status) {
										case 200:
											var fromServlet = JSON.parse(message);

											document.getElementById("errormessage").textContent = 'Everything is good here';
											document.getElementById("erroralert").innerHTML = "";

											buyOrchestrator.clearKeywordForm();
											buyOrchestrator.clearFoundAuctions();
											buyOrchestrator.clearWonAuctions();
											buyOrchestrator.clearLastClickedAuctions();
											buyOrchestrator.refreshViewOffers(fromServlet);

											updateLastClickedAuctionsLocalStorage(fromServlet.auction.auctionId);

											break;
										case 400: // bad request
											document.getElementById("errormessage").textContent = message;
											document.getElementById("erroralert").textContent = 'ERROR';
											break;
										case 401: // SC_UNAUTHORIZED
											document.getElementById("errormessage").textContent = message;
											document.getElementById("erroralert").textContent = 'ERROR';
											break;
									}
								}
							}
						);
					} else {
						form.reportValidity();
					}


				})

				form.appendChild(button);

				mainTableBody.insertRow().insertCell().appendChild(form);

				document.getElementById('foundAuctionsByKeywordSection').appendChild(mainTable);


			})
		} else {
			document.getElementById('foundAuctionsByKeywordMessage').innerHTML = "";
			document.getElementById('foundAuctionsByKeywordMessage').textContent = 'there aren\'t auction that matches with your research';
		}



	}

	this.clearViewOffers = function() {
		document.getElementById('table1Section').innerHTML = "";
		document.getElementById('table2Section').innerHTML = "";
		document.getElementById('viewOffersSection').innerHTML = "";
	}

	this.clearFoundAuctions = function() {
		document.getElementById('foundAuctionsByKeywordMessage').innerHTML = "";
		document.getElementById('foundAuctionsByKeywordSection').innerHTML = "";
	}

	this.clearLastClickedAuctions = function() {
		document.getElementById('lastClickedAuctionsMessage').innerHTML = "";
		document.getElementById('lastClickedAuctionsSection').innerHTML = "";
	}

	this.clearWonAuctions = function() {
		document.getElementById('wonAuctionsMessage').innerHTML = "";
		document.getElementById('wonAuctionsSection').innerHTML = "";
	}

	this.clearKeywordForm = function() {
		document.getElementById('search-message').innerHTML = "";
		document.getElementById('formSearchByKeyword').innerHTML = "";
	}
}