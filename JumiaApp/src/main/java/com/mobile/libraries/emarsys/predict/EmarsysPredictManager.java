/*
package com.mobile.libraries.emarsys.predict;


import android.support.v4.app.Fragment;

public class EmarsysPredictManager {

    public void sendTransactionsOf(Fragment fragment) {

        */
/*if (![viewController conformsToProtocol:@protocol(EmarsysPredictProtocolBase)]) {
            return;
        }*//*


        Session emarsysSession = new Session();
        Transaction transaction = new Transaction();
        //transaction setCart:[[RICart sharedInstance] convertItems]];

        if ([viewController conformsToProtocol:@protocol(EmarsysWebExtendProtocol)] && [viewController respondsToSelector:@selector(getDataCollection:)]) {
            transaction = [((id<EmarsysWebExtendProtocol>)viewController) getDataCollection:transaction];
        }

        if ([viewController conformsToProtocol:@protocol(EmarsysRecommendationsProtocol)]) {
            NSArray<EMRecommendationRequest *> * recommendations = [((id<EmarsysRecommendationsProtocol>)viewController) getRecommendations];
            [recommendations enumerateObjectsUsingBlock:^(EMRecommendationRequest * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                [transaction recommend:obj];
            }];
        }

        [emarsysSession sendTransaction:transaction errorHandler:^(NSError *_Nonnull error) {
            NSLog(@"value: %@", error);
            return;
        }];
    }

    + (void)setCustomer:(RICustomer *)customer {
        Session *emarsysSession = [Session sharedSession];
        if (customer.email) {
            [emarsysSession setCustomerEmail:customer.email];
        }
        if (customer.customerId) {
            [emarsysSession setCustomerID:[customer.customerId stringValue]];
        }
    }

    + (void)userLoggedOut {
        Session *emarsysSession = [Session sharedSession];
        [emarsysSession setCustomerID:nil];
        [emarsysSession setCustomerEmail:nil];
    }

}
*/
