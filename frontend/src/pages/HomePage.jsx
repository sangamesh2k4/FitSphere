import { useRef } from "react";
import "../styles/Home.css";

// Sections
import Hero from "../components/home/Hero";
import Features from "../components/home/Features";
import PublicTools from "../components/home/PublicTools";
import HowItWorks from "../components/home/HowItWorks";
import PrShowcase from "../components/home/PrShowcase";
import FinalCta from "../components/home/FinalCta";

function HomePage() {
  const howItWorksRef = useRef(null);

  const scrollToHowItWorks = () => {
    howItWorksRef.current?.scrollIntoView({
      behavior: "smooth",
      block: "start",
    });
  };

  return (
    <>

      <Hero onScrollToHowItWorks={scrollToHowItWorks} />

      <Features />

      <PublicTools />

      <section id="how-it-works" ref={howItWorksRef}>
        <HowItWorks />
      </section>

      <PrShowcase />

      <FinalCta />

    </>
  );
}

export default HomePage;