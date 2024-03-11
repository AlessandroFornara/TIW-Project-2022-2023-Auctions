export var sellOrchestrator = new SellOrchestrator();
import { updateEventsLocalStorage } from './LastEventObject.js';

function SellOrchestrator() {

	/**
	 * Clears open auctions
	 */
	this.clearOpenAuctions = function() {
		document.getElementById('openAuctionsMessage').innerHTML = "";
		document.getElementById('openAuctionsSection').innerHTML = "";
	}

	/**
	 * Refreshes open auctions
	 */
	this.refreshOpenAuctions = function(message) {
		let section = document.getElementById('openAuctionsSection'); //GET SECTION
		let openAuctions = message.openAuctions;

		openAuctions.forEach(function(auction) {
			let mainTable = document.createElement('table'); //CREATE MAIN TABLE

			let mainTableHead = mainTable.createTHead(); //CREATE MAIN TABLE THEAD
			let mainTableHeaderRow = mainTableHead.insertRow();
			mainTableHeaderRow.insertCell().textContent = 'Auction ID';
			mainTableHeaderRow.insertCell().textContent = 'Auction Details';

			let mainTableBody = mainTable.createTBody(); //CREATE MAIN TABLE TBODY
			let mainTableBodyfirstRow = mainTableBody.insertRow(); //Insert first row
			mainTableBodyfirstRow.insertCell().textContent = auction.auctionId; //Insert auctionId

			let innerTableCell = mainTableBodyfirstRow.insertCell();
			let innerTable = document.createElement('table'); //Create innerTable

			let innerTableHead = innerTable.createTHead(); //CREATE INNER TABLE THEAD
			let innerTableHeaderRow = innerTableHead.insertRow();
			innerTableHeaderRow.insertCell().textContent = 'Current Offer';
			innerTableHeaderRow.insertCell().textContent = 'Time Remaining';

			let innerTableBody = innerTable.createTBody(); //CREATE MAIN TABLE TBODY
			let innerTableBodyFirstRow = innerTableBody.insertRow(); //Insert first row
			innerTableBodyFirstRow.insertCell().textContent = auction.maxBid; //Insert maxBid


			let days = Math.floor(auction.remainingTimeMillis / (1000 * 60 * 60 * 24));
			let hours = Math.floor((auction.remainingTimeMillis / (1000 * 60 * 60)) % 24);

			let date = days + " days and " + hours + " hours";

			innerTableBodyFirstRow.insertCell().textContent = date; //Insert remainingTime //Insert remainingTime

			let innerTableBodySecondRow = innerTableBody.insertRow(); //Insert second row

			let articlesTable = document.createElement('table'); //CREATE ARTICLES TABLE
			let articlesTableHead = articlesTable.createTHead(); //CREATE ARTICLES TABLE THEAD
			let articlesTableHeaderRow = articlesTableHead.insertRow();
			articlesTableHeaderRow.insertCell().textContent = 'Article Code';
			articlesTableHeaderRow.insertCell().textContent = 'Article Name';
			articlesTableHeaderRow.insertCell().textContent = 'Image';

			let articlesTableBody = articlesTable.createTBody(); //CREATE ARTICLES TABLE TBODY

			let imageAppearCell = innerTableBodySecondRow.insertCell();

			auction.articles.forEach(function(article) {         //Insert rows
				let articlesTableRow = articlesTableBody.insertRow();
				articlesTableRow.insertCell().textContent = article.code;
				articlesTableRow.insertCell().textContent = article.name;

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

				let imageCell = articlesTableRow.insertCell();

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

			innerTableCell.appendChild(innerTable);

			innerTableBodySecondRow.insertCell().appendChild(articlesTable);

			innerTableBodySecondRow.appendChild(imageAppearCell);

			let form = document.createElement('form');
			form.id = 'auctionDetails';
			form.action = '#';

			let input = document.createElement('input');
			input.type = 'hidden';
			input.name = 'auctionID';
			input.value = auction.auctionId;
			form.appendChild(input);

			let button = document.createElement('input');
			button.id = 'detailsButton';
			button.type = 'submit';
			button.value = 'Details';

			button.addEventListener('click', (e) => {
				e.preventDefault();
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", 'OpenAuctionDetails', e.target.closest("form"),
						function(x) {
							if (x.readyState == XMLHttpRequest.DONE) {
								var message = x.responseText;
								switch (x.status) {
									case 200:
										var fromServlet = JSON.parse(message);

										document.getElementById("errormessage").textContent = 'Everything is good here';
										document.getElementById("erroralert").innerHTML = "";

										sellOrchestrator.clearOpenAuctions();
										sellOrchestrator.clearClosedAuctions();
										sellOrchestrator.clearAddAuctionAndArticlesForms();
										sellOrchestrator.goToOpenAuctionDetails(fromServlet);

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

			mainTableBodyfirstRow.insertCell().appendChild(form);

			section.appendChild(mainTable);

		});
	}

	/**
	 * Shows details of an open auction
	 */
	this.goToOpenAuctionDetails = function(message) {
		sellOrchestrator.refreshOpenAuctionDetails(message);
		if (message.auctionOffers != null) {
			sellOrchestrator.refreshOpenAuctionOffers(message);
		}
		sellOrchestrator.refreshCloseAuctionButton(message);
	}

	this.refreshCloseAuctionButton = function(message) {
		let diff = parseInt(message.difference);

		if (diff < 0) {
			let section = document.getElementById('closeAuctionForm');

			let form = document.createElement('form');
			form.action = '#';

			let input = document.createElement('input')
			input.type = 'hidden';
			input.name = 'auctionID';
			input.value = message.auction.auctionId;
			form.appendChild(input);

			let button = document.createElement('input');
			button.type = 'submit';
			button.value = 'Close Auction';

			button.addEventListener('click', (e) => {
				e.preventDefault();
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", 'CloseAuction', e.target.closest("form"),
						function(x) {
							if (x.readyState == XMLHttpRequest.DONE) {
								var message = x.responseText;
								switch (x.status) {
									case 200:
										var fromServlet = JSON.parse(message);

										sellOrchestrator.clearOpenAuctionsDetails();

										document.getElementById("errormessage").textContent = 'Everything is good here';
										document.getElementById("erroralert").innerHTML = "";

										if (fromServlet.openAuctions == null) {
											document.getElementById('openAuctionsMessage').textContent = "You have no ongoing auctions";
										} else {
											document.getElementById('openAuctionsMessage').textContent = "YOUR OPEN AUCTIONS";
											sellOrchestrator.refreshOpenAuctions(fromServlet);
										}
										if (fromServlet.closedAuctions == null) {
											document.getElementById('closedAuctionsMessage').textContent = "You have no closed auctions.";
										} else {
											document.getElementById('closedAuctionsMessage').textContent = "YOUR CLOSED AUCTIONS"
											sellOrchestrator.refreshClosedAuctions(fromServlet);
										}

										sellOrchestrator.createAddAuctionAndArticlesForms(fromServlet);

										var currentDate = new Date();
										var expirationDate = new Date(currentDate);
										expirationDate.setDate(currentDate.getDate() + 30);

										updateEventsLocalStorage(sessionStorage.getItem('username'), 'addOffer', expirationDate);
										break;
									case 400: // bad request
										document.getElementById("errormessage").textContent = message;
										document.getElementById("erroralert").textContent = 'ERROR';
										break;
									case 401: // SC_UNAUTHORIZED
										document.getElementById("errormessage").textContent = message;
										document.getElementById("erroralert").textContent = 'ERROR';
										break;
									case 500: // SC_INTERNAL_SERVER_ERROR
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


			});

			form.appendChild(button);
			section.append(form);
		}

	}


	this.refreshOpenAuctionOffers = function(message) {
		let section = document.getElementById('openAuctionOffers');

		let table = document.createElement('table');
		let thead = document.createElement('thead');
		let tbody = document.createElement('tbody');

		let headerRow = document.createElement('tr');
		let headers = ['Bidder', 'Offered Price', 'Date and Time'];

		headers.forEach(function(headerText) {
			var header = document.createElement('th');
			header.textContent = headerText;
			headerRow.appendChild(header);
		});

		thead.appendChild(headerRow);
		table.appendChild(thead);

		let auctionOffers = message.auctionOffers

		auctionOffers.forEach(function(offer) {
			let offerRow = document.createElement('tr');

			let bidderCell = document.createElement('td');
			bidderCell.textContent = offer.bidder;

			let priceCell = document.createElement('td');
			priceCell.textContent = offer.offerPrice;

			let dateTimeCell = document.createElement('td');
			dateTimeCell.textContent = offer.dateTime;

			offerRow.appendChild(bidderCell);
			offerRow.appendChild(priceCell);
			offerRow.appendChild(dateTimeCell);

			tbody.appendChild(offerRow);
		});

		table.appendChild(tbody);
		section.appendChild(table);

	}

	this.refreshOpenAuctionDetails = function(message) {
		let section = document.getElementById('openAuctionDetails');
		let mainTable = document.createElement('table');

		let mainTableHead = mainTable.createTHead();
		let headRow = mainTableHead.insertRow();

		let headings = ['ID', 'CURRENT OFFER', 'EXPIRATION', 'INITIAL PRICE', 'FINAL PRICE', 'ARTICLES'];
		for (let heading of headings) {
			let th = document.createElement('th');
			th.textContent = heading;
			headRow.appendChild(th);
		}

		let mainTableBody = mainTable.createTBody();
		let bodyRow = mainTableBody.insertRow();

		let auction = message.auction;

		let auctionId = auction.auctionId;
		let maxBid = auction.maxBid;
		let endDate = auction.endDate;
		let initialPrice = auction.initialPrice;
		let finalPrice = auction.finalPrice;

		let tdAuctionId = document.createElement('td');
		tdAuctionId.textContent = auctionId;
		bodyRow.appendChild(tdAuctionId);

		let tdMaxBid = document.createElement('td');
		tdMaxBid.textContent = maxBid;
		bodyRow.appendChild(tdMaxBid);

		let tdEndDate = document.createElement('td');
		tdEndDate.textContent = endDate;
		bodyRow.appendChild(tdEndDate);

		let tdInitialPrice = document.createElement('td');
		tdInitialPrice.textContent = initialPrice;
		bodyRow.appendChild(tdInitialPrice);

		let tdFinalPrice = document.createElement('td');
		tdFinalPrice.textContent = finalPrice;
		bodyRow.appendChild(tdFinalPrice);

		let articles = message.auctionArticles;
		let tdArticles = document.createElement('td');
		let articlesTable = document.createElement('table');

		let articlesTableHead = articlesTable.createTHead();
		let articlesHeadRow = articlesTableHead.insertRow();
		let articlesHeadings = ['Code', 'Name'];

		for (let heading of articlesHeadings) {
			let th = document.createElement('th');
			th.textContent = heading;
			articlesHeadRow.appendChild(th);
		}

		let articlesTableBody = articlesTable.createTBody();
		for (let article of articles) {
			let articleRow = articlesTableBody.insertRow();

			let tdCode = document.createElement('td');
			tdCode.textContent = article.code;
			articleRow.appendChild(tdCode);

			let tdName = document.createElement('td');
			tdName.textContent = article.name;
			articleRow.appendChild(tdName);
		}

		tdArticles.appendChild(articlesTable);
		bodyRow.appendChild(tdArticles);

		section.appendChild(mainTable);
	}

	/**
	 * Clears closed auctions
	 */
	this.clearClosedAuctions = function() {
		document.getElementById('closedAuctionsMessage').innerHTML = "";
		document.getElementById('closedAuctionsSection').innerHTML = "";
	}

	/**
	 * Refreshes closed auctions
	 */
	this.refreshClosedAuctions = function(message) {
		let section = document.getElementById('closedAuctionsSection'); //GET SECTION
		let closedAuctions = message.closedAuctions;

		closedAuctions.forEach(function(auction) {
			let mainTable = document.createElement('table'); //CREATE MAIN TABLE

			let mainTableHead = mainTable.createTHead(); //CREATE MAIN TABLE THEAD
			let mainTableHeaderRow = mainTableHead.insertRow();
			mainTableHeaderRow.insertCell().textContent = 'Auction ID';
			mainTableHeaderRow.insertCell().textContent = 'Auction Details';

			let mainTableBody = mainTable.createTBody(); //CREATE MAIN TABLE TBODY
			let mainTableBodyfirstRow = mainTableBody.insertRow(); //Insert first row
			mainTableBodyfirstRow.insertCell().textContent = auction.auctionId; //Insert auctionId

			let innerTableCell = mainTableBodyfirstRow.insertCell();
			let innerTable = document.createElement('table'); //Create innerTable

			let innerTableHead = innerTable.createTHead(); //CREATE INNER TABLE THEAD
			let innerTableHeaderRow = innerTableHead.insertRow();
			innerTableHeaderRow.insertCell().textContent = 'Final Price';
			innerTableHeaderRow.insertCell().textContent = 'Expired on';

			let innerTableBody = innerTable.createTBody(); //CREATE MAIN TABLE TBODY
			let innerTableBodyFirstRow = innerTableBody.insertRow(); //Insert first row
			innerTableBodyFirstRow.insertCell().textContent = auction.maxBid; //Insert maxBid
			innerTableBodyFirstRow.insertCell().textContent = auction.endDate; //Insert endDate

			let innerTableBodySecondRow = innerTableBody.insertRow(); //Insert second row

			let articlesTable = document.createElement('table'); //CREATE ARTICLES TABLE
			let articlesTableHead = articlesTable.createTHead(); //CREATE ARTICLES TABLE THEAD
			let articlesTableHeaderRow = articlesTableHead.insertRow();
			articlesTableHeaderRow.insertCell().textContent = 'Article Code';
			articlesTableHeaderRow.insertCell().textContent = 'Article Name';
			articlesTableHeaderRow.insertCell().textContent = 'Image';

			let articlesTableBody = articlesTable.createTBody(); //CREATE ARTICLES TABLE TBODY

			let imageAppearCell = innerTableBodySecondRow.insertCell();

			auction.articles.forEach(function(article) {         //Insert rows
				let articlesTableRow = articlesTableBody.insertRow();
				articlesTableRow.insertCell().textContent = article.code;
				articlesTableRow.insertCell().textContent = article.name;

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

				let imageCell = articlesTableRow.insertCell();

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

			innerTableCell.appendChild(innerTable);

			innerTableBodySecondRow.insertCell().appendChild(articlesTable);

			innerTableBodySecondRow.appendChild(imageAppearCell);

			let form = document.createElement('form');
			form.id = 'closedAuctionDetails';
			form.action = '#';

			let input = document.createElement('input');
			input.type = 'hidden';
			input.name = 'auctionID';
			input.value = auction.auctionId;
			form.appendChild(input);

			let button = document.createElement('input');
			button.id = 'detailsButton';
			button.type = 'submit';
			button.value = 'Details';
			form.appendChild(button);

			mainTableBodyfirstRow.insertCell().appendChild(form);

			section.appendChild(mainTable);

			button.addEventListener('click', (e) => {
				e.preventDefault();
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", 'ClosedAuctionDetails', e.target.closest("form"),
						function(x) {
							if (x.readyState == XMLHttpRequest.DONE) {
								var message = x.responseText;
								switch (x.status) {
									case 200:
										var fromServlet = JSON.parse(message);

										document.getElementById("errormessage").textContent = 'Everything is good here';
										document.getElementById("erroralert").innerHTML = "";

										sellOrchestrator.clearOpenAuctions();
										sellOrchestrator.clearClosedAuctions();
										sellOrchestrator.clearAddAuctionAndArticlesForms();
										sellOrchestrator.goToClosedAuctionDetails(fromServlet);
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
		});
	}

	this.goToClosedAuctionDetails = function(message) {
		sellOrchestrator.refreshClosedAuctionDetails(message);
		sellOrchestrator.refreshClosedAuctionArticles(message);
	}

	this.refreshClosedAuctionDetails = function(message) {

		let section = document.getElementById('closedAuctionDetails');

		let winningBidderHeading = document.createElement('h2');
		winningBidderHeading.textContent = 'Winning Bidder Details';
		section.appendChild(winningBidderHeading);

		let winningBidderList = document.createElement('ul');

		let winnerItem = document.createElement('li');
		let winnerLabel = document.createElement('span');
		winnerLabel.textContent = 'Winner: ';
		let winnerValue = document.createElement('span');
		winnerValue.textContent = message.auction.winner;
		winnerItem.appendChild(winnerLabel);
		winnerItem.appendChild(winnerValue);
		winningBidderList.appendChild(winnerItem);

		let finalPriceItem = document.createElement('li');
		let finalPriceLabel = document.createElement('span');
		finalPriceLabel.textContent = 'Final Price: ';
		let finalPriceValue = document.createElement('span');
		finalPriceValue.textContent = message.auction.finalPrice;
		finalPriceItem.appendChild(finalPriceLabel);
		finalPriceItem.appendChild(finalPriceValue);
		winningBidderList.appendChild(finalPriceItem);

		let userAddressItem = document.createElement('li');
		let userAddressLabel = document.createElement('span');
		userAddressLabel.textContent = 'User Address: ';
		let userAddressValue = document.createElement('span');
		userAddressValue.textContent = message.user.address;
		userAddressItem.appendChild(userAddressLabel);
		userAddressItem.appendChild(userAddressValue);
		winningBidderList.appendChild(userAddressItem);

		section.appendChild(winningBidderList);

		////////////////////////////////////////////////////////////////
		let table = document.createElement('table');

		let thead = document.createElement('thead');
		let headerRow = document.createElement('tr');
		let headerCells = ['Initial Price', 'Final Price', 'Expiration Date'];

		headerCells.forEach(function(cellText) {
			var th = document.createElement('th');
			th.textContent = cellText;
			headerRow.appendChild(th);
		});

		thead.appendChild(headerRow);
		table.appendChild(thead);

		let tbody = document.createElement('tbody');
		let dataRow = document.createElement('tr');

		let initialPriceCell = document.createElement('td');
		initialPriceCell.textContent = message.auction.initialPrice;
		dataRow.appendChild(initialPriceCell);

		let finalPriceCell = document.createElement('td');
		finalPriceCell.textContent = message.auction.finalPrice;
		dataRow.appendChild(finalPriceCell);

		let endDateCell = document.createElement('td');
		endDateCell.textContent = message.auction.endDate;
		dataRow.appendChild(endDateCell);

		tbody.appendChild(dataRow);
		table.appendChild(tbody);

		section.appendChild(table);

	}

	this.refreshClosedAuctionArticles = function(message) {
		let section = document.getElementById('closedAuctionArticles');
		document.createElement('h2').textContent = 'Articles';
		let table = document.createElement('table');
		let thead = table.createTHead();
		let theadRow = thead.insertRow();
		theadRow.insertCell().textContent = 'Code';
		theadRow.insertCell().textContent = 'Name';
		let tbody = table.createTBody();
		let articles = message.auctionArticle;
		articles.forEach(function(article) {
			let row = tbody.insertRow();
			row.insertCell().textContent = article.code;
			row.insertCell().textContent = article.name;
		})

		section.appendChild(table);
	}

	this.createAddAuctionAndArticlesForms = function(message) {

		let div = document.getElementById('addAuctionAndArticlesDiv');

		let form1 = document.createElement('form');
		form1.id = 'addAuctionAndArticles';
		form1.action = '#';

		let heading1 = document.createElement('h2');
		heading1.textContent = 'Create Auction';
		form1.appendChild(heading1);

		let minimumBidIncrementInput = document.createElement('input');
		minimumBidIncrementInput.type = 'text';
		minimumBidIncrementInput.name = 'minimumBidIncrement';
		minimumBidIncrementInput.pattern = '[0-9]+?';
		minimumBidIncrementInput.placeholder = 'Minimum bid: 00';
		minimumBidIncrementInput.required = true;
		form1.appendChild(minimumBidIncrementInput);

		let expirationDateInput = document.createElement('input');
		expirationDateInput.type = 'text';
		expirationDateInput.name = 'expirationDate';
		expirationDateInput.placeholder = 'Expires on: YYYY-MM-DD hh';
		expirationDateInput.maxlength = '13';
		expirationDateInput.required = true;
		expirationDateInput.pattern = '^\\d{4}-(0?[1-9]|1[0-2])-(0?[1-9]|[1-2]\\d|3[01])\\s([01]?[0-9]|2[0-3])$';
		form1.appendChild(expirationDateInput);

		let selectLabel = document.createElement('label');
		selectLabel.htmlFor = 'selectOption';
		selectLabel.textContent = 'Select one or more articles:';
		form1.appendChild(selectLabel);

		let selectInput = document.createElement('select');
		selectInput.name = 'articlesToAdd';
		selectInput.multiple = 'multiple';
		selectInput.required = true;

		let unsoldArticles = message.unsoldArticles;
		if (unsoldArticles != null) {
			unsoldArticles.forEach(function(article) {
				var option = document.createElement('option');
				option.value = article.code;
				option.textContent = article.name;
				selectInput.appendChild(option);
			});
		}

		form1.appendChild(selectInput);

		let createButton = document.createElement('input');
		createButton.id = 'button';
		createButton.type = 'submit';
		createButton.value = 'Create';
		form1.appendChild(createButton);

		createButton.addEventListener('click', (e) => {
			e.preventDefault();
			var form = e.target.closest("form");
			if (form.checkValidity()) {
				makeCall("POST", 'AddAuction', e.target.closest("form"),
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							var message = x.responseText;
							switch (x.status) {
								case 200:
									var fromServlet = JSON.parse(message);

									document.getElementById("errormessage").textContent = 'Everything is good here';
									document.getElementById("erroralert").innerHTML = "";

									sellOrchestrator.clearOpenAuctions();
									sellOrchestrator.clearAddAuctionAndArticlesForms();

									sellOrchestrator.refreshOpenAuctions(fromServlet);
									sellOrchestrator.createAddAuctionAndArticlesForms(fromServlet);

									var currentDate = new Date();
									var expirationDate = new Date(currentDate);
									expirationDate.setDate(currentDate.getDate() + 30);

									updateEventsLocalStorage(sessionStorage.getItem('username'), 'auctionCreated', expirationDate);

									console.log(localStorage.getItem('lastEventsArray'));
									break;
								case 400: // bad request
									document.getElementById("errormessage").textContent = message;
									document.getElementById("erroralert").textContent = 'ERROR';
									break;
								case 401: // SC_UNAUTHORIZED
									document.getElementById("errormessage").textContent = message;
									document.getElementById("erroralert").textContent = 'ERROR';
									break;
								case 500: // SC_INTERNAL_SERVER_ERROR
									document.getElementById("errormessage").textContent = message;
									document.getElementById("erroralert").textContent = 'ERROR';
									break;
								case 502: // SC_BAD_GATEWAY
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


		});

		let form2 = document.createElement('form');
		form2.id = 'addAuctionAndArticles';
		form2.action = '#';

		let heading2 = document.createElement('h2');
		heading2.textContent = 'Add Article';
		form2.appendChild(heading2);

		let articleNameInput = document.createElement('input');
		articleNameInput.type = 'text';
		articleNameInput.name = 'articleName';
		articleNameInput.placeholder = 'Article Name';
		articleNameInput.required = true;
		form2.appendChild(articleNameInput);

		let articleDescriptionInput = document.createElement('input');
		articleDescriptionInput.type = 'text';
		articleDescriptionInput.name = 'articleDescription';
		articleDescriptionInput.placeholder = 'Description';
		articleDescriptionInput.maxlength = '200';
		articleDescriptionInput.required = true;
		form2.appendChild(articleDescriptionInput);

		let articlePriceInput = document.createElement('input');
		articlePriceInput.type = 'text';
		articlePriceInput.name = 'articlePrice';
		articlePriceInput.pattern = '[0-9]+?';
		articlePriceInput.placeholder = 'Price: 00';
		articlePriceInput.required = true;
		form2.appendChild(articlePriceInput);

		let addButton = document.createElement('input');
		addButton.id = 'button';
		addButton.type = 'submit';
		addButton.value = 'Add';
		form2.appendChild(addButton);

		addButton.addEventListener('click', (e) => {
			e.preventDefault();
			var form = e.target.closest("form");
			if (form.checkValidity()) {
				makeCall("POST", 'AddArticle', e.target.closest("form"),
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							var message = x.responseText;
							switch (x.status) {
								case 200:
									var fromServlet = JSON.parse(message);

									document.getElementById("errormessage").textContent = 'Everything is good here';
									document.getElementById("erroralert").innerHTML = "";

									sellOrchestrator.clearAddAuctionAndArticlesForms();
									sellOrchestrator.createAddAuctionAndArticlesForms(fromServlet);

									var currentDate = new Date();
									var expirationDate = new Date(currentDate);
									expirationDate.setDate(currentDate.getDate() + 30);

									updateEventsLocalStorage(sessionStorage.getItem('username'), 'addArticle', expirationDate);

									break;
								case 400: // bad request
									document.getElementById("errormessage").textContent = message;
									document.getElementById("erroralert").textContent = 'ERROR';
									break;
								case 401: // SC_UNAUTHORIZED
									document.getElementById("errormessage").textContent = message;
									document.getElementById("erroralert").textContent = 'ERROR';
									break;
								case 500: // SC_INTERNAL_SERVER_ERROR
									document.getElementById("errormessage").textContent = message;
									document.getElementById("erroralert").textContent = 'ERROR';
									break;
								case 502: // SC_BAD_GATEWAY
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


		});

		div.appendChild(form1);
		div.appendChild(form2);

	}

	this.clearAddAuctionAndArticlesForms = function() {
		document.getElementById('addAuctionAndArticlesDiv').innerHTML = "";
	}


	this.clearOpenAuctionsDetails = function() {
		document.getElementById('openAuctionDetails').innerHTML = "";
		document.getElementById('openAuctionOffers').innerHTML = "";
		document.getElementById('closeAuctionForm').innerHTML = "";
	}

	this.clearClosedAuctionsDetails = function() {
		document.getElementById('closedAuctionDetails').innerHTML = "";
		document.getElementById('closedAuctionArticles').innerHTML = "";
	}


}