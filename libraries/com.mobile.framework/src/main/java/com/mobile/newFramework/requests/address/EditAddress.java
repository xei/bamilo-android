package com.mobile.newFramework.requests.address;

import android.content.Context;

import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
<<<<<<< HEAD
import com.mobile.newFramework.objects.SuperCustomerNewsletterSubscription;
=======
>>>>>>> 1bfc976d4045a485f9fddf12611aba4dbb41bb84
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

<<<<<<< HEAD
public class EditAddress extends BaseRequest<SuperCustomerNewsletterSubscription> {
=======
public class EditAddress extends BaseRequest {
>>>>>>> 1bfc976d4045a485f9fddf12611aba4dbb41bb84

    public EditAddress(Context context, RequestBundle requestBundle, AigResponseCallback requester) {
        super(context, requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter(mContext, mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
<<<<<<< HEAD
        service.createAddress(mRequestBundle.getData(), this);
=======
        service.editAddress(mRequestBundle.getData(), this);
>>>>>>> 1bfc976d4045a485f9fddf12611aba4dbb41bb84
    }
}
