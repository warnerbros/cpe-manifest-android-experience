package com.wb.nextgen.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgen.R;

import com.wb.nextgen.NextGenExtraActivity;
import com.wb.nextgen.util.PicassoTrustAll;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/13/16.
 */
public class NextGenActorListFragment extends NextGenExtraLeftListFragment {

    public static class Actor {
        public final String realName;
        public final String characterName;
        public final String avatarURL;
        public final String biograph;
        public Actor(String realName, String characterName, String avatarURL, String biograph) {
            this.realName = realName;
            this.characterName = characterName;
            this.avatarURL = avatarURL;
            this.biograph = biograph;
        }
    }

    private static List<Actor> actors = new ArrayList<Actor>();

    static {
        actors.add(new Actor("HENRY CAVILL", "Clark Kent/Kal-El", "http://ia.media-imdb.com/images/M/MV5BMTM1NDAwODcxMl5BMl5BanBnXkFtZTcwMjM4NTIwNw@@._V1_UY317_CR1,0,214,317_AL_.jpg", "Henry William Dalgliesh Cavill is a British actor. He was born on the Bailiwick of Jersey, a British Crown dependency in the Channel Islands.\n" +
                "\n" +
                "His mother, Marianne (Dalgliesh), a housewife, was also born on Jersey, and is of Irish, Scottish, and English ancestry. Henry's father, Colin Richard Cavill, a stockbroker, is of English origin (born in Chester, England). Henry is the second youngest son, with four brothers. He was privately educated at St. Michael's Preparatory School in Saint Saviour, Jersey before attending Stowe School in Buckinghamshire, England."));
        actors.add(new Actor("AMY ADAMS", "Lois Lane", "http://ia.media-imdb.com/images/M/MV5BMjE4NjExMjI1OF5BMl5BanBnXkFtZTcwODc0MjY2OQ@@._V1_UX214_CR0,0,214,317_AL_.jpg", "Amy Lou Adams was born in Italy, to American parents Kathryn (Hicken) and Richard Kent Adams, while her father was a U.S. serviceman. She was raised in a Mormon family of seven children in Castle Rock, Colorado, and has English, as well as smaller amounts of Danish, Swiss-German, and Norwegian, ancestry.\n" +
                "\n" +
                "Adams sang in the school choir at Douglas County High School and was an apprentice dancer at a local dance company, with the ambition of becoming a ballerina. However, she worked as a greeter at The Gap and as a Hooters hostess to support herself before finding work as a dancer at Boulder's Dinner Theatre and Country Dinner Playhouse in such productions as \"Brigadoon\" and \"A Chorus Line\". It was there that she was spotted by a Minneapolis dinner-theater director who asked her to move to Chanhassen, Minnesota for more regional dinner theater work."));
        actors.add(new Actor("MICHAEL SHANNON", "General Zod", "http://ia.media-imdb.com/images/M/MV5BMTQzODk4MjU1NV5BMl5BanBnXkFtZTcwMDYwNjQzMg@@._V1_UY317_CR1,0,214,317_AL_.jpg", "Michael Corbett Shannon was born and raised in Lexington, Kentucky, the son of Geraldine Hine, a lawyer, and Donald Sutherlin Shannon, an accounting professor at DePaul University. His grandfather was entomologist Raymond Corbett Shannon.\n" +
                "\n" +
                "Shannon began his professional stage career in Chicago. His first acting role was in \"Winterset\" at the Illinois Theatre Center. Over the next several years he continued working on the stage with such companies as Steppenwolf, The Next Lab and the Red Orchid Theatre. He subsequently relocated to London for a year, and performed on stage in London's West End in such productions as \"Woyzeck\", \"Killer Joe\" and \"Bug\"."));
        actors.add(new Actor("RUSSELL CROWE", "Jor-El", "http://ia.media-imdb.com/images/M/MV5BMTQyMTExNTMxOF5BMl5BanBnXkFtZTcwNDg1NzkzNw@@._V1_UX214_CR0,0,214,317_AL_.jpg", "Russell Ira Crowe was born in Wellington, New Zealand, to Jocelyn Yvonne (Wemyss) and John Alexander Crowe, both of whom catered movie sets. His maternal grandfather, Stanley Wemyss, was a cinematographer. Crowe's recent ancestry includes Welsh (where his paternal grandfather was born, in Wrexham), English, Irish, Scottish, Norwegian, Swedish, and Maori (one of Crowe's maternal great-grandmothers, Erana Putiputi Hayes Heihi, was Maori).\n" +
                "\n" +
                "Crowe's family moved to Australia when he was a small child, and Russell got the acting bug early in life. Beginning as a child star on a local Australian TV show, Russell's first big break came with two films ... the first, Romper Stomper (1992), gained him a name throughout the film community in Australia and the neighboring countries. The second, The Sum of Us (1994), helped put him on the American map, so to speak. Sharon Stone heard of him from Romper Stomper (1992) and wanted him for her film, The Quick and the Dead (1995). But filming on The Sum of Us (1994) had already begun. Sharon is reported to have held up shooting until she had her gunslinger-Crowe, for her film. With The Quick and the Dead (1995) under his belt as his first American film, the second was offered to him soon after. Virtuosity (1995), starring Denzel Washington, put Russell in the body of a Virtual Serial Killer, Sid6.7 ... a role unlike any he had played so far. Virtuosity (1995), a Sci-Fi extravaganza, was a fun film and, again, opened the door to even more American offers. L.A. Confidential (1997), Russell's third American film, brought him the US fame and attention that his fans have felt he deserved all along. Missing the Oscar nod this time around, he didn't seem deterred and signed to do his first film with The Walt Disney Company, Mystery, Alaska (1999). He achieved even more success and awards for his performances in Gladiator (2000) and A Beautiful Mind (2001)."));
        actors.add(new Actor("DIANE LANE", "Martha Kent", "http://ia.media-imdb.com/images/M/MV5BMjE4ODQ5NTgxNl5BMl5BanBnXkFtZTcwNDkwMDgyMg@@._V1_UX214_CR0,0,214,317_AL_.jpg", "Diane Lane was born on January 22, 1965, in New York. She is the daughter of acting coach Burton Eugene \"Burt\" Lane and nightclub singer/centerfold Colleen Farrington. Her parents' families were both from the state of Georgia. Diane was acting from a very young age and made her stage debut at the age of six. Her work in such acclaimed theater productions as \"The Cherry Orchard\" and \"Medea\" led to her being called to Hollywood. She was 13 when she was cast by director George Roy Hill in his wonderful 1979 film A Little Romance (1979), opposite Sir Laurence Olivier. The film only did so-so commercially, but Olivier praised his young co-star, calling her the new Grace Kelly. After her well-received debut, Diane found herself on magazine covers all over the world, including \"Time\", which declared her the \"new young acting sensation\". However, things quietened down a bit when she found herself in such critical and financial flops as Touched by Love (1980), Cattle Annie and Little Britches (1981), National Lampoon's Movie Madness (1982), Ladies and Gentlemen, the Fabulous Stains (1982) and, most unmemorably, Six Pack (1982), all of which failed to set her career on fire.\n" +
                "\n" +
                "She also made several TV movies during this period, but it was in 1983 that she finally began to fulfill the promise of stardom that had earlier been predicted for her. Acclaimed director Francis Ford Coppola took note of Diane's appeal and cast her in two \"youth\"-oriented films based on S.E. Hinton novels. Indeed, Rumble Fish (1983) and The Outsiders (1983) have become cult classics and resulted in her getting a loyal fan base. The industry was now taking notice of Diane Lane, and she soon secured lead roles in three big-budget studio epics. She turned down the first, Splash (1984) (which was a surprise hit for Daryl Hannah). Unfortunately, the other two were critical and box-office bombs: Walter Hill's glossy rock 'n' roll fable Streets of Fire (1984) was not the huge summer success that many had thought it would be, and the massively troubled Coppola epic The Cotton Club (1984) co-starring Richard Gere was also a high-profile flop. The back-to-back failure of both of these films could have ended her career there and then -- but thankfully it didn't. Possibly \"burned out\" by the lambasting these films received and unhappy with the direction her career was taking, she \"retired\" from the film business at age 19, saying that she had forgotten what she had started acting for. She stayed away from the screen for the next three years. Ironically, the two films that were the main causes of her \"retirement\" have since grown in popularity, and \"Streets of Fire\" especially seems to have found the kind of audience it couldn't get when it was first released."));
        actors.add(new Actor("KEVIN COSTER", "Jonathan Kent", "http://ia.media-imdb.com/images/M/MV5BMTQ0MDU1OTEyNF5BMl5BanBnXkFtZTgwNjI0MTk2MDE@._V1_UY317_CR0,0,214,317_AL_.jpg", "Kevin was born in Lynwood, California, on January 18, 1955, the third child of Bill Costner, a ditch digger and ultimately an electric line servicer for Southern California Edison, and Sharon Costner (née Tedrick), a welfare worker. His older brother, Dan, was born in 1950. A middle brother died at birth in 1953. His father's job required him to move regularly, which caused Kevin to feel like an Army kid, always the new kid at school, which led to him being a daydreamer. As a teen, he sang in the Baptist church choir, wrote poetry, and took writing classes. At 18, he built his own canoe and paddled his way down the rivers that Lewis & Clark followed to the Pacific. Despite his present height, he was only 5'2\" when he graduated high school. Nonetheless, he still managed to be a basketball, football and baseball star. In 1973, he enrolled at California State University at Fullerton, where he majored in business. During that period, Kevin decided to take acting lessons five nights a week. He graduated with a business degree in 1978 and married his college sweetheart, Cindy Costner. He initially took a marketing job in Orange County. Everything changed when he accidentally met Richard Burton on a flight from Mexico. Burton advised him to go completely after acting if that is what he wanted. He quit his job and moved to Hollywood soon after. He drove a truck, worked on a deep sea fishing boat, and gave bus tours to stars' homes before finally making his own way into the films. After making one soft core sex film, he vowed to not work again if that was the only work he could do. He didn't work for nearly six years, while he waited for a proper break. That break came with The Big Chill (1983), even though his scenes ended up on the cutting room floor -- he was remembered by director Lawrence Kasdan when he decided to make Silverado (1985). Costner's career took off after that."));
        actors.add(new Actor("ANTJE TRAUE", "Faora-Ul", "http://ia.media-imdb.com/images/M/MV5BMTgzMjY4MjUyMF5BMl5BanBnXkFtZTgwMjc1ODU1MDE@._V1_UY317_CR12,0,214,317_AL_.jpg", "Antje Traue was born in Mittweida, Saxony, East Germany. Her mother was a musician and dancer. Traue was raised speaking Russian. She discovered her love for acting at an early age. As a teenager, Traue attended the \"International Munich Art Lab\", where she was cast in the lead role in the theatre play, \"West End Opera\". For four years, she toured with the ensemble cast throughout Europe and then to New York. In 2002, Traue moved to Berlin and landed roles in both feature films and television projects, including Kleinruppin forever (2004), Berlin am Meer (2008) and Phantom Pain (2009). In 2008, she was cast as the female lead in the international science fiction film, Pandorum (2009), opposite Dennis Quaid and Ben Foster. In 2010, she was filming on Renny Harlin's 5 Days of War (2011), alongside Val Kilmer and Rupert Friend."));
    }

    protected void onListItmeClick(View v, final int position, long id){
        if (getActivity() instanceof NextGenExtraActivity){
            NextGenActorDetailFragment target = new NextGenActorDetailFragment();
            NextGenActorDetailFragment.NextGenExtraDetialInterface obj = new NextGenActorDetailFragment.NextGenExtraDetialInterface() {
                @Override
                public String getThumbnailImageUrl() {
                    return ((Actor)getListItemAtPosition(position)).avatarURL;
                }

                @Override
                public String getDetailText() {
                    return ((Actor)getListItemAtPosition(position)).biograph;
                }
            };
            target.setDetailObject(obj);
            ((NextGenExtraActivity)getActivity()).transitRightMainFragment(target);
        }
    }

    protected int getNumberOfColumns(){
        return 1;
    }

    protected int getListItemCount() {
        return actors.size();
    }

    protected Object getListItemAtPosition(int i) {
        return actors.get(i);
    }

    protected int getListItemViewId() {
        return R.layout.next_gen_actors_row;
    }

    protected void fillListRowWithObjectInfo(View rowView, Object item) {


        ImageView avatarImg = (ImageView) rowView.findViewById(R.id.next_gen_actor_avatar);
        TextView realNameTxt = (TextView) rowView.findViewById(R.id.next_gen_actor_real_name);
        TextView characterNameTxt = (TextView) rowView.findViewById(R.id.next_gen_actor_character_name);

        Actor thisActor = (Actor) item;
        if(!thisActor.realName.equals(realNameTxt.getText())) {
            realNameTxt.setText(thisActor.realName);
            characterNameTxt.setText(thisActor.characterName);
            PicassoTrustAll.loadImageIntoView(getActivity(), thisActor.avatarURL, avatarImg);
        }
    }

    protected String getHeaderText(){
        return "Actors";
    }

    protected int getHeaderChildenCount(int header){
        return getListItemCount();
    }

    protected int getHeaderCount(){
        return 1;
    }
}
