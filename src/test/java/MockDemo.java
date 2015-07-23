/**
 * Created by Grinyov Vitaliy on 23.07.15.
 */

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MockDemo {

    /*
    Этот пример демонстрирует создание мок-итератора и
    «заставляет» его возвращать «Hello» при первом вызове
    метода next(). Последующие вызовы этого метода будут возвращать «World».
    После этого мы можем выполнять обычные assert'ы.
     */

    @Test
    public void iterator_will_return_hello_world() {
        //подготавливаем
        Iterator i = mock(Iterator.class);
        when(i.next()).thenReturn("Hello").thenReturn("World");
        //выполняем
        String result = i.next()+" "+i.next();
        //сравниваем
        assertEquals("Hello World", result);
    }

    /*
    Здесь мы создаём объект-заглушку Comparable, и возвращаем 1 в случае,
    если он сравнивается с определённым String-значением («Test», в данном случае).
     */
    @Test
    public void with_arguments() {
        Comparable c = mock(Comparable.class);
        when(c.compareTo("Test")).thenReturn(1);
        assertEquals(1, c.compareTo("Test"));
    }

    /*
    Если метод имеет какие-то аргументы, но Вам всё равно, что будет в них
     передано или предсказать это невозможно, то используйте anyInt()
     (и альтернативные значения для других типов).
     */
    @Test
    public void with_unspecified_arguments() {
        Comparable c = mock(Comparable.class);
        when(c.compareTo(anyInt())).thenReturn(-1);
        assertEquals(-1, c.compareTo(5));
    }

    /*
    Альтернативным синтаксисом в этой ситуации будет
    doReturn(result).when(mock_object).void_method_call();.
    Вместо возврата результата Вы также можете использовать .thenThrow()
    или doThrow() для void-методов.
     */
    @Test(expected=IOException.class)
    public void OutputStreamWriter_rethrows_an_exception_from_OutputStream()
            throws IOException {
        OutputStream mock = mock(OutputStream.class);
        OutputStreamWriter osw = new OutputStreamWriter(mock);
        doThrow(new IOException()).when(mock).close();
        osw.close();
    }

    /*
    В этом примере выбрасывается IOException, когда в заглушке OutputStream
    вызывается метод close. Мы с лёгкостью проверяем, что OutputStreamWriter
    пробрасывает такой эксепшн наружу.
    Чтобы проверить, что метод действительно был вызван (типичное использование
    объектов-заглушек), мы можем использовать verify(mock_object).method_call;.
     */
    @Test
    public void OutputStreamWriter_Closes_OutputStream_on_Close()
            throws IOException {
        OutputStream mock = mock(OutputStream.class);
        OutputStreamWriter osw = new OutputStreamWriter(mock);
        osw.close();
        verify(mock).close();
    }

    /*
    В этом примере мы проверяем, что OutputStreamWriter совершает вызов
    метода close() во вложенном OutputStream.
    Вы можете использовать аргументы в методах и подстановки для них,
    такие как anyInt(), как в одном из предыдущих примеров. Стоит отметить,
    что Вы не можете смешивать литералы и матчеры. Используйте матчер eq(value)
    для конвертирования литерала в матчер, который сравнит значение.
    Mockito предоставляет уйму уже готовых матчеров, но иногда Вам может потребоваться
    более гибкий подход. К примеру, OutputStreamWriter будет буферизовать
    вывод и затем передавать его обёрнутому объекту при заполнении буфера,
    но мы не знаем, насколько длинный буфер нам собираются передать.
    Здесь мы не можем использовать сравнение на равенство. Однако,
    мы можем запилить собственный матчер:
     */

    @Test
    public void OutputStreamWriter_Buffers_And_Forwards_To_OutputStream()
            throws IOException {
        OutputStream mock = mock(OutputStream.class);
        OutputStreamWriter osw = new OutputStreamWriter(mock);
        osw.write('a');
        osw.flush();
        // не можем делать так, потому что мы не знаем,
        // насколько длинным может быть массив
        // verify(mock).write(new byte[]{'a'},0,1);

        BaseMatcher<byte[]> arrayStartingWithA = new BaseMatcher<byte[]>() {
           //@Override
            public void describeTo(Description description) {
                // пустота
            }

            // Проверяем, что первый символ - это A
           // @Override
            public boolean matches(Object item) {
                byte[] actual = (byte[]) item;
                return actual[0] == 'a';
            }
        };
        // проверяем, что первый символ массива - это A, и что другие два аргумента равны 0 и 1.
       // verify(mock).write(argThat(arrayStartingWithA), eq(0), eq(1));
    }
}
