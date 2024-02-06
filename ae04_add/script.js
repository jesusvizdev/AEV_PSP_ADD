const container = document.querySelector(".container");
const search = document.querySelector(".search-box button");
const weatherBox = document.querySelector(".weather-box");
const weatherDetails = document.querySelector(".weather-details");
const error404 = document.querySelector(".not-found");
const save = document.querySelector(".btn-save");
const searchDb = document.querySelector(".container-db button");

let data = null;

/**
 * Función para realizar una búsqueda de clima basada en la ciudad proporcionada.
 * La búsqueda se realiza en la API OpenWeatherMap con una apiKey proporcionada.
 *
 */
search.addEventListener("click", () => {
  const apiKey = "5ad2cfbb038aa24c5d2ca8c25241cfc4";
  const city = document.querySelector(".search-box input").value;

  if (city === "") return;

  axios
    .get(
      `https://api.openweathermap.org/data/2.5/weather?q=${city}&units=metric&appid=${apiKey}`
    )
    .then((response) => {
      error404.style.display = "none";
      save.style.display = "block";

      const image = document.querySelector(".weather-box img");
      const temperature = document.querySelector(".temperature");
      const description = document.querySelector(".description");
      const humidity = document.querySelector(".humidity span");
      const wind = document.querySelector(".wind span");

      const date = new Date();
      const day = date.toISOString().split("T")[0];
      const hour = date.toTimeString().split(" ")[0];
      console.log(day);
      console.log(hour);
      const weather = response.data.weather[0].main;

      if (weather === "Clear") {
        image.src = "img/clear.png";
      } else if (weather === "Rain") {
        image.src = "img/rain.png";
      } else if (weather === "Snow") {
        image.src = "img/snow.png";
      } else if (weather === "Clouds") {
        image.src = "img/cloud.png";
      } else if (weather === "Haze") {
        image.src = "img/mist.png";
      } else {
        image.src = "";
      }

      temperature.innerHTML = `${parseInt(
        response.data.main.temp
      )}<span>ºC</span>`;
      description.innerHTML = city;
      document.querySelector(".search-box input").value = "";
      humidity.innerHTML = `${parseInt(response.data.main.humidity)}%`;
      wind.innerHTML = `${parseInt(response.data.wind.speed)}Km/h`;

      weatherBox.style.display = "";
      weatherDetails.style.display = "";
      container.style.height = "590px";

      data = {
        nombre: city,
        tiempo: weather,
        temperatura: parseInt(response.data.main.temp),
        humedad: parseInt(response.data.main.humidity),
        viento: parseInt(response.data.wind.speed),
        fecha: day,
        hora: hour,
      };
    })

    .catch((error) => {
      if (error.response.status === 404) {
        container.style.height = "400px";
        save.style.display = "none";
        weatherBox.style.display = "none";
        weatherDetails.style.display = "none";
        error404.style.display = "block";
        document.querySelector(".search-box input").value = "";
      }
    });
});

/**
 * Función para guardar los datos del clima en la base de datos.
 */
save.addEventListener("click", () => {
  $.ajax({
    type: "POST",
    url: "http://127.0.0.1:5500/ae04_add/post.php",
    data: data,
    success: function (response) {
      alert(response);
    },
    error: function () {
      alert("Error");
    },
  });
});

/**
 * Función para buscar datos de clima almacenados en la base de datos para una ciudad específica.
 */
searchDb.addEventListener("click", () => {
  const city = document
    .querySelector(".container-db input")
    .value.toLowerCase();

  document.querySelector(".container-db input").value = "";
  if (city === "") return;

  data = {
    nombre: city,
  };

  $.ajax({
    type: "GET",
    url: "http://127.0.0.1:5500/ae04_add/get.php",
    data: data,
    dataType: "json",
    success: function (response) {
      const ul = document.querySelector("#list-results");
      const divResults = document.querySelector(".db-results");
      const primerNodoEsParrafo =
        divResults.firstElementChild.tagName.toLowerCase() === "p";
      const ultimoNodoEsParrafo =
        divResults.lastElementChild.tagName.toLowerCase() === "p";

      if (ultimoNodoEsParrafo) {
        divResults.lastElementChild.remove();
      }

      if (!primerNodoEsParrafo) {
        const p = `<p>REGISTROS OBTENIDOS PARA: <span id="city">${city.toUpperCase()}</span></p>`;
        divResults.insertAdjacentHTML("afterbegin", p);
      } else {
        divResults.firstElementChild.innerHTML = `<p>REGISTROS OBTENIDOS PARA: <span id="city">${city.toUpperCase()}</span></p>`;
      }

      ul.innerHTML = "";

      if (response.length === 0) {
        const p = `<p style="color: red; font-weight: bold; font-size:14px">No se ha encontrado información para esa ciudad en la Base de Datos...</p>`;
        divResults.insertAdjacentHTML("beforeend", p);
      }

      response.map((item) => {
        const listItem = `
        <li>
            <p>Id: ${item.id}</p>
            <p>Fecha: <span>${item.fecha}</span></p>
            <p>Hora: <span>${item.hora}</span></p>
            <p>Tiempo: <span>${item.tiempo}</span></p>
            <p>Temperatura: <span>${item.temperatura}ºC</span></p>
            <p>Humedad: <span>${item.humedad}%</span></p>
            <p>Viento: <span>${item.viento}Km/h</span></p>
        </li>
        `;
        ul.insertAdjacentHTML("beforeend", listItem);
      });
    },
    error: function () {
      alert("Error");
    },
  });
});
