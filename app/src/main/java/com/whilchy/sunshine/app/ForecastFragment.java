package com.whilchy.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by daniel on 13/12/14.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Dummy data
        String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

        // Now we create the Array Adapter and pass it the dummy data we've just created
        // we will use it to populate the ListView
        ArrayAdapter<String> forecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecast
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView forecastListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(forecastAdapter);



        return rootView;
    }

    /**
     * Perfoms the AsyncTask off the Main Thread on a Background Thread
     */
    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            // Connects to OpenWeatherMaps API to retrieve the data
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contains the JSON response as String
            String forecastJsonString = null;

            try {
                // URL for the OpenWeatherMap query
                // More info at http://openweathermap.org/API
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=Oliveira+de+Azemeis&mode=json&units=metric&cnt=7");

                // Creates the GET request to OpenWeatherMap
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a stream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(buffer == null) {
                    return null;    // No buffer
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0) {
                    return null;    // Stream empty
                }
                forecastJsonString = buffer.toString();

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, String.format("Malformed URL: [%s]", e.getMessage()), e);
                // If we cannot wasn't successfull there's no point in attempting parse it
                return null;
            } catch (IOException e) {
                Log.e(LOG_TAG, String.format("Connection Error: [%s]", e.getMessage()), e);
                // If we cannot wasn't successfull there's no point in attempting parse it
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, String.format("Error closing stream: [%s]", e.getMessage()) , e);
                    }
                }
            }
            return null;
        }
    }
}
