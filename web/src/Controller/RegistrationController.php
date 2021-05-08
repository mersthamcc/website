<?php

namespace App\Controller;

use App\ApiClient\MemberRegistrationService;
use App\ApiClient\RegistrationService;
use App\ApiClient\UserService;
use App\Entity\Member;
use App\Entity\RegistrationBasket;
use App\Entity\Subscription;
use App\Forms\Membership\ChooseMembershipForm;
use App\Forms\Membership\MemberListForm;
use App\Forms\Membership\RegistrationForm;
use App\Menus\TopMenuProvider;
use Knp\Menu\FactoryInterface;
use mysql_xdevapi\Session;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Uid\Uuid;

class RegistrationController extends AbstractController implements
    TopMenuProvider
{
    private const BASKET_SESSION_KEY = "REGISTRATION_BASKET";
    private const CURRENT_SUBSCRIPTION_ID = "CURRENT_SUBSCRIPTION_ID";

    /**
     * @var RegistrationService $registrationService
     */
    private $registrationService;

    /**
     * @var LoggerInterface $logger
     */
    private $logger;

    /**
     * @var UserService $userService
     */
    private $userService;

    /**
     * @var SessionInterface $session
     */
    private $session;

    /**
     * RegistrationController constructor.
     * @param MemberRegistrationService $registrationService
     * @param LoggerInterface $logger
     * @param UserService $userService
     * @param SessionInterface $session
     */
    public function __construct(
        MemberRegistrationService $registrationService,
        LoggerInterface $logger,
        UserService $userService,
        SessionInterface $session
    ) {
        $this->registrationService = $registrationService;
        $this->logger = $logger;
        $this->userService = $userService;
        $this->session = $session;
    }

    /**
     * @Route("/register", name="registration")
     * @param Request $request
     * @return Response
     */
    public function index(Request $request): Response
    {
        $me = $this->userService->me();

        $form = $this->createForm(
            MemberListForm::class,
            $this->getRegistrationBasket()
        );

        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $buttonName = $form->getClickedButton()->getName();
            switch ($buttonName) {
                case "add-member":
                    $this->logger->debug("Form submitted");
                    $id = Uuid::v4()->toRfc4122();
                    $this->session->set(self::CURRENT_SUBSCRIPTION_ID, $id);
                    return $this->redirectToRoute("choose-membership");
                case "reset":
                    $this->session->remove(self::BASKET_SESSION_KEY);
                    break;
                default:
                    $this->logger->debug(
                        "Unhandled submit button: " . $buttonName
                    );
                    break;
            }
        }
        return $this->render("registration/index.html.twig", [
            "form" => $form->createView(),
        ]);
    }

    /**
     * @Route("/register/choose-membership", name="choose-membership", methods={"POST", "GET"})
     * @param Request $request
     * @return Response
     */
    public function chooseMembership(Request $request): Response
    {
        $basket = $this->getRegistrationBasket();
        $id = $this->session->get(self::CURRENT_SUBSCRIPTION_ID);
        if (isset($basket[$id])) {
            $subscription = $basket[$id];
        } else {
            $subscription = new Subscription();
            $subscription->setRegistrationId($id);
            $basket[$id] = $subscription;
            $this->saveRegistrationBasket($basket);
        }

        $this->logger->debug("Setting up subscription form for request", [
            "subscription" => $subscription,
            "request" => $request,
            "content" => $request->getContent(),
        ]);

        $form = $this->createForm(ChooseMembershipForm::class, $subscription, [
            "choices" => $this->registrationService->getMembershipCategories(),
        ]);

        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $subscription = $form->getData();
            $this->logger->debug("Subscription form data processed", [
                "subscription" => $subscription,
                "request" => $request,
            ]);

            $priceListItemId = $subscription->getPriceListItemId();
            $this->logger->debug("Pricelist item ID = $priceListItemId", [
                "subscription" => $subscription,
                "request" => $request,
            ]);
            // $subscription->setPriceListItem($this->registrationService->)
            $subscription->setMembershipCategory(
                $this->registrationService->getCategoryByPriceListItemId(
                    $priceListItemId
                )
            );

            $basket[$id] = $subscription;
            $this->saveRegistrationBasket($basket);

            return $this->redirectToRoute("add-member", []);
        }
        return $this->render("registration/index.html.twig", [
            "form" => $form->createView(),
        ]);
    }

    /**
     * @Route("/register/add-member", name="add-member")
     * @param Request $request
     * @return Response
     */
    public function addMember(Request $request): Response
    {
        $basket = $this->getRegistrationBasket();
        $id = $this->session->get(self::CURRENT_SUBSCRIPTION_ID);
        $subscription = $basket[$id];
        $category = $subscription->getMembershipCategory();
        $subscription->setPriceListItemId($request->get("priceListItemId"));
        $form = $this->createForm(RegistrationForm::class, $subscription, [
            "category" => $this->registrationService->getCategory(
                $category->getKey()
            ),
            "translation_domain" => "membership",
        ]);

        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            /**
             * @var Subscription $subscription
             */
            $subscription = $form->getData();
            $subscription->setDate(new \DateTime());
            $basket[$id] = $subscription;

            $this->saveRegistrationBasket($basket);
            return $this->redirectToRoute("registration");
        }
        return $this->render("registration/index.html.twig", [
            "form" => $form->createView(),
        ]);
    }

    /**
     * @return RegistrationBasket
     */
    private function getRegistrationBasket()
    {
        return $this->session->get(
            self::BASKET_SESSION_KEY,
            new RegistrationBasket()
        );
    }

    /**
     * @param RegistrationBasket $basket
     */
    private function saveRegistrationBasket(RegistrationBasket $basket)
    {
        $this->session->set(self::BASKET_SESSION_KEY, $basket);
    }

    public static function getFrontEndMenuItems(
        FactoryInterface $factory
    ): array {
        return [];
    }

    public static function getTopMenuItems(FactoryInterface $factory): array
    {
        return [
            $factory
                ->createItem("register", [
                    "route" => "registration",
                ])
                ->setAttribute("sortorder", 10),
        ];
    }

    public static function getContentAdminMenuItems(
        FactoryInterface $factory
    ): array {
        return [];
    }

    public static function getAdministrationMenuItems(
        FactoryInterface $factory
    ): array {
        return [];
    }

    public static function getConfigurationAdminMenuItems(
        FactoryInterface $factory
    ): array {
        // TODO: Implement getConfigurationAdminMenuItems() method.
    }
}
