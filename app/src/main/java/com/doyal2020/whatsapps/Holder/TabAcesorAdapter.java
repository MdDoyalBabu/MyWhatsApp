package com.doyal2020.whatsapps.Holder;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.doyal2020.whatsapps.ChatsFragment;
import com.doyal2020.whatsapps.ContactsFragment;
import com.doyal2020.whatsapps.GroupFragment;
import com.doyal2020.whatsapps.RequestFragment;

public class TabAcesorAdapter extends FragmentPagerAdapter {


    public TabAcesorAdapter(FragmentManager fm) {


        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)

        {

            case 0:
                ChatsFragment chatsFragment=new ChatsFragment();
                return chatsFragment;
              case 1:
                  GroupFragment groupFragment=new GroupFragment();
                  return groupFragment;

              case 2:
                  ContactsFragment contactsFragment=new ContactsFragment();
                  return contactsFragment;
           case 3:
                  RequestFragment requestFragment=new RequestFragment();
                  return requestFragment;

                default:
                    return null;

        }
    }

    @Override
    public int getCount() {

        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position)

        {

            case 0:
                return "Chats";

            case 1:
                return "Group";
            case 2:

                return "Contacts";
            case 3:

                return "Request";

            default:
                return null;

        }



    }
}
