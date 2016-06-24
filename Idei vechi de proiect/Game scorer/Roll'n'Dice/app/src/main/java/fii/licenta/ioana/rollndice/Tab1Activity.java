package fii.licenta.ioana.rollndice;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Tab1Activity extends Fragment {
    private HttpRequest request;
    private TextView textView;
    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tab1, container, false);
        textView = (TextView)view.findViewById(R.id.tab1_textView);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateRandom();
            }
        };
        button = (Button)view.findViewById(R.id.tab1_button);
        button.setOnClickListener(listener);
        return view;
    }

    public void generateRandom(){
        request = (HttpRequest)new HttpRequest().execute();
        request.setTextView(textView);
    }

}
