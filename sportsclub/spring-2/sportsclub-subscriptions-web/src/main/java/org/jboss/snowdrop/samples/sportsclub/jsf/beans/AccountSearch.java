package org.jboss.snowdrop.samples.sportsclub.jsf.beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.jboss.snowdrop.samples.sportsclub.domain.entity.Account;
import org.jboss.snowdrop.samples.sportsclub.ejb.SubscriptionService;

/**
 * @author <a href="mailto:mariusb@redhat.com">Marius Bogoevici</a>
 */
public class AccountSearch extends ExtendedDataModel<Account> {

    @EJB
    private SubscriptionService subscriptionService;

    private String name;

    private int currentPage = 1;


    private Long currentId;

    private Map<Long, Account> accountsMap = new HashMap<Long, Account>();

    private boolean editing;

    private Collection<Object> selection;

    private int rowIndex = -1;

    private int rowCount;

    private boolean dirty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String searchAccounts() {
        rowCount = subscriptionService.countAccountsBySubscriberName(name);
        this.dirty=true;
        currentPage = 1;
        return "success";
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        this.dirty = true;
    }

    @Override
    public Object getRowKey() {
        return currentId;
    }

    @Override
    public void setRowKey(Object key) {
        currentId = (Long) key;
    }

    @Override
    public boolean isRowAvailable() {
        return currentId != null;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    public boolean isSearchInfoAvailable() {
        return name != null;
    }

    @Override
    public Account getRowData() {
        return accountsMap.get(currentId);
    }

    @Override
    public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) {
        int firstResult = ((SequenceRange) range).getFirstRow();
        int maxResults = ((SequenceRange) range).getRows();

        if (this.dirty) {
            List<Account> list = subscriptionService.findAccountsBySubscriberName(name, firstResult, maxResults);
            accountsMap = new LinkedHashMap<Long, Account>();
            for (Account row : list) {
                Long id = row.getId();
                accountsMap.put(id, row);
            }
            dirty = false;
        }

        for (Map.Entry<Long, Account> accountEntry : accountsMap.entrySet()) {
            visitor.process(context, accountEntry.getKey(), argument);
        }

    }

    @Override
    public int getRowIndex() {
        return rowIndex;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public Object getWrappedData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWrappedData(Object data) {
        throw new UnsupportedOperationException();
    }

    public Account getCurrentAccount() {
        Long selectedKey = getSelectedKey();

        return selectedKey != null? accountsMap.get(selectedKey):null;
    }

    private Long getSelectedKey() {
        if (selection == null || selection.size() == 0)
            return null;
        else
            return (Long) selection.iterator().next();
    }

    public void setSelection(Collection<Object> selection) {
        this.selection = selection;
    }

    public Collection<Object> getSelection() {
        return selection;
    }

    public String closeAccount() {
        subscriptionService.closeAccount(getCurrentAccount());
        selection.clear();
        this.dirty = true;
        this.rowCount = subscriptionService.countAccountsBySubscriberName(name);
        return "closed";
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public boolean isEditing() {
        return editing;
    }

    public void saveCurrent() {
        subscriptionService.updateAccount(getCurrentAccount());
        this.editing = false;
    }


    public void selectionListener(AjaxBehaviorEvent event) {
        // do nothing
    }
}
