import { sellOrchestrator } from './SellOrchestrator.js';
import { buyOrchestrator } from './BuyOrchestrator.js';
import { findLastEventByUsername, cancelIfExpired } from './LastEventObject.js';

{
	var pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "Login.html";
		} else {
			console.log("Loading Page");
			pageOrchestrator.start(); // initialize the components
		}
	}, false);

	function PageOrchestrator() {

		cancelIfExpired();

		var lastEventsString = localStorage.getItem('lastEventsArray');
		var username = sessionStorage.getItem('username');
		console.log(lastEventsString);

		var lastEventsArray;
		var event;

		if (lastEventsString !== undefined) {
			lastEventsArray = JSON.parse(lastEventsString);
			event = findLastEventByUsername(lastEventsArray, username);
		}
		this.start = function() {

			if (event != null) {

				console.log(event);
				if (event.lastEvent === 'auctionCreated') {
					this.startSell();
				} else {
					this.startBuy();
				}
			}
			else {
				this.startBuy();
			}
			document.getElementById('SellButton').addEventListener('click', (e) => {

				e.preventDefault();
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", 'GoToSell', null,
						function(x) {
							if (x.readyState == XMLHttpRequest.DONE) {
								var message = x.responseText;
								switch (x.status) {
									case 200:
										var fromServlet = JSON.parse(message);

										pageOrchestrator.clearPage();

										document.getElementById('pageTitle').textContent = "SELL YOUR ARTICLES";

										document.getElementById('welcome-message').textContent = 'Hi ' + username + ', in this page you can find your auctions and create new ones';

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

										break;
									case 401: // unauthorized
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
				}
			})

			document.getElementById('BuyButton').addEventListener('click', (e) => {

				e.preventDefault();
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", 'GoToBuy', null,
						function(x) {
							if (x.readyState == XMLHttpRequest.DONE) {
								var message = x.responseText;
								switch (x.status) {
									case 200:
										var fromServlet = JSON.parse(message);

										pageOrchestrator.clearPage();

										buyOrchestrator.createKeywordForm();

										document.getElementById('welcome-message').textContent = 'Hi ' + username + ', you can use this page to buy items';

										document.getElementById('pageTitle').textContent = "PURCHASE";

										let wonAuctionsMessage = document.getElementById('wonAuctionsMessage');
										if (fromServlet.wonAuctions.length === 0) {
											wonAuctionsMessage.textContent = 'You haven\'t won any auction yet';
										}
										else {
											wonAuctionsMessage.textContent = 'Here are the auctions you won';
											buyOrchestrator.refreshWonAuctions(fromServlet);
										}
										//buyOrchestrator.refreshLastClickedAuctions(fromServlet);

										break;
									case 401: // unauthorized
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
				}

				var ids = localStorage.getItem('lastClickedAuctionsIDs');
				console.log(ids);

				if (ids !== undefined && ids != null) {

					let tmpForm = document.createElement('form');

					let tmpInput = document.createElement('input');
					tmpInput.type = 'hidden';
					tmpInput.name = 'arrayIds';
					tmpInput.value = ids;
					tmpForm.appendChild(tmpInput);

					makeCall("POST", 'FetchLastClickedAuctions', tmpForm,
						function(x) {
							if (x.readyState == XMLHttpRequest.DONE) {
								var message = x.responseText;
								switch (x.status) {
									case 200:
										var fromServlet = JSON.parse(message);

										document.getElementById("errormessage").textContent = 'Everything is good here';
										document.getElementById("erroralert").innerHTML="";

										buyOrchestrator.refreshLastClickedAuctions(fromServlet);

										break;
									case 401: // unauthorized
										document.getElementById("errormessage").textContent = message;
										document.getElementById("erroralert").textContent = 'ERROR';
										break;
									case 500: // SC_INTERNAL_SERVER_ERROR
										document.getElementById("errormessage").textContent = message;
										document.getElementById("erroralert").textContent = 'ERROR';
										break;
								}
							}
							return false;
						});
				}
				else {
					document.getElementById('lastClickedAuctionsMessage').innerHTML = "";
				}
			});
		}

		this.clearPage = function() {

			document.getElementById('pageTitle').innerHTML = "";
			document.getElementById('welcome-message').innerHTML = "";
			document.getElementById('minimum-raise-message').innerHTML = "";

			sellOrchestrator.clearOpenAuctions();
			sellOrchestrator.clearClosedAuctions();
			sellOrchestrator.clearOpenAuctionsDetails();
			sellOrchestrator.clearClosedAuctionsDetails();
			sellOrchestrator.clearAddAuctionAndArticlesForms();

			document.getElementById('badOfferMessage').innerHTML = "";
			buyOrchestrator.clearViewOffers();
			buyOrchestrator.clearFoundAuctions();
			buyOrchestrator.clearLastClickedAuctions();
			buyOrchestrator.clearWonAuctions();
			buyOrchestrator.clearKeywordForm();

			document.getElementById("errormessage").textContent = 'Everything is good here';
			document.getElementById("erroralert").innerHTML="";
		}

		this.startBuy = function() {
			//assign messages
			document.getElementById('pageTitle').textContent = "PURCHASE";
			document.getElementById('welcome-message').textContent = 'Hi ' + username + ', you can use this page to buy items';
			document.getElementById("errormessage").textContent = "Everything is good here";
			document.getElementById("erroralert").innerHTML="";

			// inizializzare le section: FORM, WONAUCTION -> metodi refresh

			makeCall("POST", 'GoToBuy', null,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								var fromServlet = JSON.parse(message);

								document.getElementById("errormessage").textContent = 'Everything is good here';
								document.getElementById("erroralert").innerHTML="";

								buyOrchestrator.createKeywordForm();

								let wonAuctionsMessage = document.getElementById('wonAuctionsMessage');
								if (fromServlet.wonAuctions.length === 0) {
									wonAuctionsMessage.textContent = 'You haven\'t won any auction yet';
								}
								else {
									wonAuctionsMessage.textContent = 'Here are the auctions you won';
									buyOrchestrator.refreshWonAuctions(fromServlet);
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
					return false;
				});

			var ids = localStorage.getItem('lastClickedAuctionsIDs');
			console.log(ids);

			if (ids !== undefined && ids != null) {

				let tmpForm = document.createElement('form');

				let tmpInput = document.createElement('input');
				tmpInput.type = 'hidden';
				tmpInput.name = 'arrayIds';
				tmpInput.value = ids;
				tmpForm.appendChild(tmpInput);

				makeCall("POST", 'FetchLastClickedAuctions', tmpForm,
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							var message = x.responseText;
							switch (x.status) {
								case 200:
									var fromServlet = JSON.parse(message);

									document.getElementById("errormessage").textContent = 'Everything is good here';
									document.getElementById("erroralert").innerHTML="";

									buyOrchestrator.refreshLastClickedAuctions(fromServlet);

									break;
								case 401: // unauthorized
									document.getElementById("errormessage").textContent = message;
									document.getElementById("erroralert").textContent = 'ERROR';
									break;
								case 500: // SC_INTERNAL_SERVER_ERROR
									document.getElementById("errormessage").textContent = message;
									document.getElementById("erroralert").textContent = 'ERROR';
									break;
							}
						}
						return false;
					});
			}
			else {

				document.getElementById('lastClickedAuctionsMessage').innerHTML = "";
			}
		}

		this.startSell = function() {

			document.getElementById('pageTitle').textContent = "SELL YOUR ARTICLES";
			document.getElementById('welcome-message').textContent = 'Hi ' + username + ', in this page you can find your auctions and create new ones';
			document.getElementById("errormessage").textContent = 'Everything is good here';
			document.getElementById("erroralert").innerHTML="";


			makeCall("POST", 'GoToSell', null,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								var fromServlet = JSON.parse(message);

								document.getElementById("errormessage").textContent = 'Everything is good here';
								document.getElementById("erroralert").innerHTML="";

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

								break;
							case 401: // unauthorized
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
		}

	}


}