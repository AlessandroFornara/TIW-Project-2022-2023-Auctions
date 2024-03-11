
function LastEventObject(username, lastEvent, date) {
	this.username = username;
	this.lastEvent = lastEvent;
	this.date = date;
}

function LastClickedAuctionsObject(username, id, date) {
	this.username = username;
	this.id = id;
	this.date = date;
}

export function updateEventsLocalStorage(username, lastEvent, date) {

	var lastEventsArrayString = localStorage.getItem('lastEventsArray');
	var eventsArray;

	if (lastEventsArrayString !== undefined) {
		eventsArray = JSON.parse(lastEventsArrayString);
		localStorage.removeItem('lastEventsArray');
	}
	var user = sessionStorage.getItem('username');

	// array non vuoto
	if (eventsArray != null) {

		var newLastEvent = findLastEventByUsername(eventsArray, user);

		// nome utente già presente
		if (newLastEvent != null) {

			newLastEvent.lastEvent = lastEvent;
			newLastEvent.date = date;

			localStorage.setItem('lastEventsArray', JSON.stringify(eventsArray));
		}
		else {

			var firstLastEvent = new LastEventObject(username, lastEvent, date);
			eventsArray.push(firstLastEvent);

			localStorage.setItem('lastEventsArray', JSON.stringify(eventsArray));
		}
	}
	else {

		//creo l'array
		var array = [];
		var firstLastEvent = new LastEventObject(username, lastEvent, date);
		array.push(firstLastEvent);

		localStorage.setItem('lastEventsArray', JSON.stringify(array));

		console.log(JSON.stringify(array));
	}
}

export function findLastEventByUsername(lastEvents, username) {

	if (lastEvents != null) {

		for (let i = 0; i < lastEvents.length; i++) {
			if (lastEvents[i].username === username) {
				return lastEvents[i];
			}
		}
	}
	return null;
}

export function updateLastClickedAuctionsLocalStorage(toAdd) {

	var lastClickedAuctions = localStorage.getItem('lastClickedAuctionsIDs');

	var currentDate = new Date();
	var expirationDate = new Date(currentDate);
	expirationDate.setDate(currentDate.getDate() + 30);

	if (lastClickedAuctions !== undefined && lastClickedAuctions != null) {

		lastClickedAuctions = JSON.parse(lastClickedAuctions);
		localStorage.removeItem('lastClickedAuctionsIDs');

		if (findLastClickedAuctionByID(lastClickedAuctions, toAdd) === false) {

			lastClickedAuctions.push(new LastClickedAuctionsObject(sessionStorage.getItem('username'), toAdd, expirationDate));
			localStorage.setItem('lastClickedAuctionsIDs', JSON.stringify(lastClickedAuctions));
		}
		else {
			localStorage.setItem('lastClickedAuctionsIDs', JSON.stringify(lastClickedAuctions));
			return;
		};
	}
	else {

		var array = [];
		array.push(new LastClickedAuctionsObject(sessionStorage.getItem('username'), toAdd, expirationDate));

		localStorage.setItem('lastClickedAuctionsIDs', JSON.stringify(array));
	}
}

export function cancelIfExpired() {
	let lastEventsString = localStorage.getItem('lastEventsArray');
	let currentDate = new Date();

	if (lastEventsString !== undefined && lastEventsString != null) {
		let lastEventsArray = JSON.parse(lastEventsString);
		localStorage.removeItem('lastEventsArray');

		for (let i = lastEventsArray.length - 1; i >= 0; i--) {
			let eventDate = new Date(lastEventsArray[i].date);

			if (eventDate < currentDate) {
				lastEventsArray.splice(i, 1);
			}
		}

		localStorage.setItem('lastEventsArray', JSON.stringify(lastEventsArray));
	}

	let lastCString = localStorage.getItem('lastClickedAuctionsIDs');

	if (lastCString !== undefined && lastCString != null) {
		let lastC = JSON.parse(lastCString);
		localStorage.removeItem('lastClickedAuctionsIDs');

		for (let i = lastC.length - 1; i >= 0; i--) {
			let auctionDate = new Date(lastC[i].date);

			if (auctionDate < currentDate) {
				lastC.splice(i, 1);
			}
		}

		localStorage.setItem('lastClickedAuctionsIDs', JSON.stringify(lastC));
	}
}



export function findLastClickedAuctionByID(lastClicked, id) {

	if (lastClicked != null) {

		for (let i = 0; i < lastClicked.length; i++) {
			if (lastClicked[i].id === id && lastClicked[i].username === sessionStorage.getItem('username')) {
				return true;
			}
		}
	}
	else {
		return null;
	}
	return false;
}


