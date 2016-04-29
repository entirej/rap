package org.entirej.applicationframework.rwt.renderers.item;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.graphics.Image;
import org.entirej.framework.core.EJMessage;

public class ControlDecorationSupport
{
   public  static void  handleMessage(ControlDecoration decoration,EJMessage message  )
    {
         decoration.setDescriptionText("");
         if (message!=null)
         {
             
             switch (message.getLevel())
             {
                 case ERROR:
                     decoration.setImage(getDecorationImage(FieldDecorationRegistry.DEC_ERROR));
                     break;
                 case DEBUG:
                 case WARNING:
                     decoration.setImage(getDecorationImage(FieldDecorationRegistry.DEC_WARNING));
                     break;
                 case HINT:
                 case MESSAGE:
                     decoration.setImage(getDecorationImage(FieldDecorationRegistry.DEC_INFORMATION));
                     break;

                 default:
                     break;
             }
             if(message.getMessage()!=null)
             {
                 decoration.setDescriptionText(message.getMessage());
             }
             
             decoration.show();
         }
         else
         {
             decoration.hide();
         }
    }
     
     private static Image  getDecorationImage(String image)
     {
         FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
         return registry.getFieldDecoration(image).getImage();
     }
}
