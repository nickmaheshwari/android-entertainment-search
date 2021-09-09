Android Entertainment Search:

This is a relatively simple Android application that implements OMBD's API to search for entertainment. It is a multi-tiered application, consisting of a model tier, a view-model tier, and a view tier. There are a few POJO models to represent the JSON data returned by the API calls, and two different views, one for the search screen and one for the item details screen. The model-view layer of the stack passes data between the layers and performs any backend logic. The best way to run this project is to clone it and open in Android Studio. After setting up a test device within the IDE, you can run the application within Android Studio. 



API  docs:
http://omdbapi.com/

Example API call:
https://www.omdbapi.com/?s=alien&page=1&apikey=dc077f32
