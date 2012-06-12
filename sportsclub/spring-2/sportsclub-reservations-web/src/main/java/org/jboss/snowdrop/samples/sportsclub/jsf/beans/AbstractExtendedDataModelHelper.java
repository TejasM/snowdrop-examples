package org.jboss.snowdrop.samples.sportsclub.jsf.beans;

import java.util.Collection;
import java.util.Map;

import org.ajax4jsf.model.ExtendedDataModel;

/**
 * Abstract helper class for JSF backing beans serving {#link ExtendedDataModel} implementations. 
 *
 * @author <a href="mailto:lvlcek@redhat.com">Lukas Vlcek</a>
 */
public abstract class AbstractExtendedDataModelHelper<T> extends ExtendedDataModel<T>
{
   private int currentPage = 1;
   private int currentRow;
   private Long currentId;
   private Long currentRowCount;
   private Collection<Object> selection;

   abstract public Map<Long,T> getDomainObjectMap();

   abstract public Long getCurrentRowCount();

   public Long getCurrentId()
   {
      return currentId;
   }

   @Override
   public Object getRowKey()
   {
      return currentId;
   }

   @Override
   public void setRowKey(Object key)
   {
      if (key != null)
         currentId = (Long) key;
   }

   @Override
   public int getRowIndex()
   {
      return currentRow;
   }

   @Override
   public void setRowIndex(int rowIndex)
   {
      this.currentRow = rowIndex;
   }

   @Override
   public Object getWrappedData()
   {
      throw new UnsupportedOperationException("Not supported");
   }

   @Override
   public void setWrappedData(Object data)
   {
      throw new UnsupportedOperationException("Not supported");
   }

   @Override
   public int getRowCount()
   {
      if (currentRowCount == null)
      {
         currentRowCount = getCurrentRowCount();
      }
      return currentRowCount.intValue();
   }

   public void resetCurrentRowCount()
   {
      currentRowCount = null;
      currentPage = 1;
   }

   @Override
   public T getRowData()
   {
      return getDomainObjectMap().get(currentId);
   }

   @Override
   public boolean isRowAvailable()
   {
      if (currentId == null)
         return false;
      if (getDomainObjectMap().containsKey(currentId))
         return true;
      return false;
   }

   public int getCurrentPage()
   {
      return currentPage;
   }

   public void setCurrentPage(int currentPage)
   {
      this.currentPage = currentPage;
   }

   public Collection<Object> getSelection()
   {
      return selection;
   }

   public void setSelection(Collection<Object> selection)
   {
      this.selection = selection;
   }

   public void clearSelection() {
       if (this.selection != null) {
           this.selection.clear();
       }
   }
}
