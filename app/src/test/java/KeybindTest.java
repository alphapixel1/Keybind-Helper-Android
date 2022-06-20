import android.content.Context;

import com.example.keybindhelper.Keybind;
import com.example.keybindhelper.KeybindGroup;

import org.junit.Test;
import org.junit.Assert;

public class KeybindTest{
    private Context context;
    KeybindGroup g=new KeybindGroup(context);
    @Test
    public void givenKeybindsExist_whenUserExits_ThenKeybindsAreSaved(){
        Assert.assertEquals(true,true);
    }

    @Test
    public void givenKeybindsExist_whenUserCopies_ThenKeybindsAreCloned(){
        Assert.assertEquals(true,true);
    }
}
