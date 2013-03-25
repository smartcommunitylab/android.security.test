/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.android.security.test;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.Toast;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.ac.authenticator.AMSCAccessProvider;
import eu.trentorise.smartcampus.ac.model.UserData;

public class ACActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		try {
			Constants.setAuthUrl(this, "https://ac.smartcampuslab.it/accesstoken-provider-dev/ac");
		} catch (NameNotFoundException e1) {
        	Toast.makeText(ACActivity.this, "Configuration failure: "+e1.getMessage(), Toast.LENGTH_LONG).show();
        	return;
		}
        
        setContentView(R.layout.main);

        final SCAccessProvider provider = new AMSCAccessProvider();
        final String token = provider.readToken(this, null);
        UserData data = provider.readUserData(this, null);
        if (token == null && provider.readToken(this, Constants.TOKEN_TYPE_ANONYMOUS)==null) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Do you want to register?");
        	builder.setPositiveButton(android.R.string.yes, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						provider.getAuthToken(ACActivity.this, null);
					} catch (Exception e) {
			        	Toast.makeText(ACActivity.this, "Failure: "+e.getMessage(), Toast.LENGTH_LONG).show();
					}
				}
			});
        	builder.setNegativeButton(android.R.string.no, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						provider.getAuthToken(ACActivity.this, Constants.TOKEN_TYPE_ANONYMOUS);
					} catch (Exception e) {
			        	Toast.makeText(ACActivity.this, "Failure: "+e.getMessage(), Toast.LENGTH_LONG).show();
					}
				}
			});
        	builder.create().show();
        } else {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Do you want to promote?");
        	builder.setPositiveButton(android.R.string.yes, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						provider.promote(ACActivity.this, null, token);
					} catch (Exception e) {
			        	Toast.makeText(ACActivity.this, "Failure: "+e.getMessage(), Toast.LENGTH_LONG).show();
					}
				}
			});
        	builder.setNegativeButton(android.R.string.no, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
        	builder.create().show();
        }
    }
    
    protected void testSCProviderFromActivity(SCAccessProvider provider) {
       try {
			String token = provider.getAuthToken(this, null);
			if (token != null) {
	            Toast.makeText(this, "done: "+token, Toast.LENGTH_LONG).show();
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SCAccessProvider.SC_AUTH_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, data.getExtras().getString(AccountManager.KEY_AUTHTOKEN), Toast.LENGTH_LONG).show();
			} else if (resultCode == RESULT_CANCELED) {
		        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this,data.getExtras().getString(AccountManager.KEY_AUTH_FAILED_MESSAGE),Toast.LENGTH_LONG).show();
			}
		}
	}

}
