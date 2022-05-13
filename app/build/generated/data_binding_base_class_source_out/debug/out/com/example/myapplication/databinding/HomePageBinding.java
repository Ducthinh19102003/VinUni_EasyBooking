// Generated by view binder compiler. Do not edit!
package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.myapplication.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class HomePageBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final CardView bookingCard;

  @NonNull
  public final LinearLayout layoutUserInformation;

  @NonNull
  public final TextView txtEmail;

  @NonNull
  public final TextView txtUserName;

  private HomePageBinding(@NonNull LinearLayout rootView, @NonNull CardView bookingCard,
      @NonNull LinearLayout layoutUserInformation, @NonNull TextView txtEmail,
      @NonNull TextView txtUserName) {
    this.rootView = rootView;
    this.bookingCard = bookingCard;
    this.layoutUserInformation = layoutUserInformation;
    this.txtEmail = txtEmail;
    this.txtUserName = txtUserName;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static HomePageBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static HomePageBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.home_page, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static HomePageBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.booking_card;
      CardView bookingCard = ViewBindings.findChildViewById(rootView, id);
      if (bookingCard == null) {
        break missingId;
      }

      id = R.id.layout_user_information;
      LinearLayout layoutUserInformation = ViewBindings.findChildViewById(rootView, id);
      if (layoutUserInformation == null) {
        break missingId;
      }

      id = R.id.txt_email;
      TextView txtEmail = ViewBindings.findChildViewById(rootView, id);
      if (txtEmail == null) {
        break missingId;
      }

      id = R.id.txt_user_name;
      TextView txtUserName = ViewBindings.findChildViewById(rootView, id);
      if (txtUserName == null) {
        break missingId;
      }

      return new HomePageBinding((LinearLayout) rootView, bookingCard, layoutUserInformation,
          txtEmail, txtUserName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}